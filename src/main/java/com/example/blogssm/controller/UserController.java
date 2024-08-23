package com.example.blogssm.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.map.MapUtil;
import com.example.blogssm.common.*;
import com.example.blogssm.constants.Const;
import com.example.blogssm.entity.UserInfo;
import com.example.blogssm.entity.vo.UserinfoVO;
import com.example.blogssm.service.ArticleService;
import com.example.blogssm.service.UserService;
import com.google.code.kaptcha.Producer;
import org.apache.catalina.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;

import static jdk.nashorn.internal.objects.Global.undefined;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2023/04/13  18:58
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private UserCacheUtils userCacheUtils;
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    Producer producer;
    @Autowired
    private IpInfoService ipInfoService;
    private static final Logger loginLogger = LogManager.getLogger("loginLogger");
    @GetMapping("/captcha")
    public AjaxResult Captcha(HttpServletRequest request) throws IOException {
        // 获取旧的key
        String oldKey = (String) request.getSession().getAttribute("captchaKey");
        if (oldKey != null) {
            // 如果存在旧的key，从Redis中删除它
            redisCache.hdel(Const.CAPTCHA_KEY, oldKey);
        }
        // 生成新的key和验证码
        String newKey = UUID.randomUUID().toString();
        String code = producer.createText();

        // 将新的key存储在会话中
        request.getSession().setAttribute("captchaKey", newKey);
        // 将新的验证码存储在Redis中
        redisCache.hset(Const.CAPTCHA_KEY, newKey, code, 120);
        // 生成验证码图片
        BufferedImage image = producer.createImage(code);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);

        String str = "data:image/jpeg;base64,";
        String base64Img = str + Base64.getEncoder().encodeToString(outputStream.toByteArray());

        return AjaxResult.success(
                MapUtil.builder()
                        .put("userKey", newKey)
                        .put("captcherImg", base64Img)
                        .build()
        );
    }


    @RequestMapping("/reg")
    public AjaxResult reg(@RequestBody UserInfo request, HttpServletRequest httpRequest) throws IOException {
        //非空校验
        if(!StringUtils.hasLength(request.getUsername())||!StringUtils.hasLength(request.getPassword())){
            return AjaxResult.fail(-1,"非法参数");
        }else{
            //    查询数据库
            UserInfo user = userService.getUserByName(request.getUsername());
            if (user != null && user.getId() > 0) {
                //说明有重名的了，就要返回错误
                return AjaxResult.fail(409,"用户名已被注册");
            }
            UserInfo userInfo=new UserInfo();
            userInfo.setAddress(ipInfoService.getIpInfo( httpRequest));
//            System.out.println(ipInfoService.getIpInfo( httpRequest));
            userInfo.setUsername(request.getUsername());

            userInfo.setPassword(PasswordUtils.encrypt(request.getPassword()));
            // 记录日志
            String mes=userInfo.getUsername()+"注册成功";
            String logMessage = String.format("%s\t%s\t%s",
                    mes,
                    userInfo.getAddress(),
                    new Date()
            );
            loginLogger.info(logMessage);
            return AjaxResult.success(userService.reg(userInfo));
        }
    }

@GetMapping("/getUserByToken")
    public  AjaxResult login(@RequestHeader("Authorization")String token){
        Integer id=redisCache.hget(token, Const.USER_ID);
        if(id==null){
            //缓存未命中就重置缓存数据
            //下面是从数据库写回缓存的时候，从数据库通过token解析的用户名查询的
            UserInfo user= userCacheUtils.RESetUserCache(token);
            return AjaxResult.success(user);
        }
    return  AjaxResult.success(userCacheUtils.getUser(token));
}
//@RequestMapping("/showInfo")
//    public AjaxResult showInfo(HttpServletRequest request){
//    UserinfoVO userinfoVO=new UserinfoVO();
////1.得到当前用户（从session中获取）
// UserInfo userInfo= UserSessionUtils.getSessUser(request);
// if(userInfo==null){
//     return AjaxResult.fail(-1,"非法请求");
// }
// //spring提供的深克隆方法
//    BeanUtils.copyProperties(userInfo,userinfoVO);
//    //2.得到用户发表文章的总数
//    userinfoVO.setArticleCount(articleService.getArtCountByUid(userInfo.getId()));
//    return AjaxResult.success(userinfoVO);
//}
//@RequestMapping("/logout")
//    public AjaxResult logout(HttpSession session){
//        session.removeAttribute(AppVarible.USER_SESSION_KEY);
//        return AjaxResult.success(1);
//}
@RequestMapping("/getuserbyid")
    public AjaxResult getUserById(Integer id){
        if(id==null ||id<=0){
            return AjaxResult.fail(-1,"无效id");
        }
        UserInfo userinfo=userService.getUserById(id);
        if(userinfo==null || userinfo.getId()<=0){
            return AjaxResult.fail(-1,"非法参数");
        }
        userinfo.setPassword("");
        UserinfoVO userinfoVO=new UserinfoVO();
        BeanUtils.copyProperties(userinfo,userinfoVO);
        userinfoVO.setArticleCount(articleService.getArtCountByUid(id));
        return AjaxResult.success(userinfoVO);
}
}
