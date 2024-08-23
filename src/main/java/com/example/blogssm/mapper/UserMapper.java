package com.example.blogssm.mapper;

import com.example.blogssm.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2023/04/13  18:59
 */
@Mapper
public interface UserMapper {
    /**
     * 进行注册的
     * @param userInfo
     * @return
     */
    int reg(UserInfo userInfo);
    UserInfo getUserByName(@Param("username") String username);
    UserInfo getUserById(@Param("id") Integer id);

    int uploadAvatarById(String username,String avater);

    String getNameById(Integer id);
}
