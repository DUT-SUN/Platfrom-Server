package com.example.blogssm.common;

import com.example.blogssm.constants.Const;
import com.example.blogssm.entity.UserInfo;
import com.example.blogssm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述
 * <p>用于用户缓存的写回和读取
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/04/04  19:10
 */
@Component
public class UserCacheUtils {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RedisCache redisCache;
    //用户缓存未命中的重新防止缓存
    public UserInfo RESetUserCache(String token) {
        try{
            //查询用户
            UserInfo user = userService.getUserByName(jwtUtils.getClaimsByToken(token).getSubject());
            //设置缓存的hash属性
            redisCache.hset(token, Const.USER_ID, user.getId(),3600 );
            redisCache.hset(token, Const.USER_AVATER, user.getAvater(), 3600);
            redisCache.hset(token, Const.USER_STATE, user.getState(), 3600);
            redisCache.hset(token, Const.USER_NAME, user.getUsername(), 3600);
            return user;
        }catch (Exception e){
            throw new RuntimeException("更新用户缓存失败，数据库 or redis Exception");
        }
    }
    public UserInfo getUser(String token){
        //用户缓存命中了返回
        List<Object> UserPropList = new ArrayList<>();
        UserPropList.add(Const.USER_ID);
        UserPropList.add(Const.USER_NAME);
        UserPropList.add(Const.USER_AVATER);
        UserPropList.add(Const.USER_STATE);
        List<Object> values = redisCache.getMultiCacheMapValue(token, UserPropList);

        // 创建一个新的UserInfo对象
        UserInfo userInfo = new UserInfo();

        // 从values列表中获取用户信息，并设置到userInfo对象中
        userInfo.setId((Integer) values.get(0));
        userInfo.setUsername((String) values.get(1));
        userInfo.setAvater((String) values.get(2));
        userInfo.setState((Integer) values.get(3));
        // 返回userInfo对象
        return userInfo;
    }


}
