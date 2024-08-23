package com.example.blogssm.Handler;

import cn.hutool.json.JSONUtil;
import com.example.blogssm.Exception.CaptchaException;
import com.example.blogssm.common.AjaxResult;
import com.example.blogssm.common.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/03/31  19:14
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
//        System.out.println("进入成功了");
        // 生成JWT，并放置到响应体data中
        String jwt = jwtUtils.generateToken(authentication.getName());
//        httpServletResponse.setHeader(jwtUtils.getHeader(), jwt);
        Map<String, String> tokenData = new HashMap<>();
        tokenData.put("token", jwt);
        AjaxResult result =  AjaxResult.success(200,"SuccessLogin",tokenData);
        // 将JWT添加到响应体中
        outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}

