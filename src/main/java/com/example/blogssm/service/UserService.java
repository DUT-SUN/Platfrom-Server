package com.example.blogssm.service;

import com.example.blogssm.entity.UserInfo;
import com.example.blogssm.mapper.UserMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2023/04/13  19:06
 */
@Service
public class UserService  {
    @Autowired
    private UserMapper userMapper;

    public int reg(UserInfo userInfo){
        return userMapper.reg(userInfo);
    }
    public UserInfo getUserByName(String username){return userMapper.getUserByName(username);}
    public String getNameById(Integer id){
        return userMapper.getNameById(id);
    }
    public UserInfo getUserById( Integer id){return userMapper.getUserById(id);}
    public int UploadAvater(String username,String avater){return userMapper.uploadAvatarById(username,avater);}
    public String getUserAuthorityInfo(Integer userId) {
        UserInfo user = getUserById(userId.intValue());
        if (user != null) {
            switch (user.getState()) {
                case 0:
                    return "ROLE_banned";
                case 1:
                    return "ROLE_normal";
                case 2:
                    return "ROLE_admin";
                default:
                    return "ROLE_unknown";
            }
        } else {
            throw new UsernameNotFoundException("用户不存在");
        }
    }
}
