package com.example.blogssm.controller;

import com.example.blogssm.entity.UserInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2023/09/15  13:39
 */
class UserControllerTest {

    @Test
    void login() {
        UserInfo userInfo=new UserInfo();
        userInfo.setPassword("admin");
        userInfo.setUsername("2=2");
    }
}