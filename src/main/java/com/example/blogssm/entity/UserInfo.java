package com.example.blogssm.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2023/04/13  19:00
 */
@Data
public class UserInfo implements Serializable {
    private Integer id;
    private String username;
    private String password;
    private String avater;
    private String address;
    private LocalDateTime createtime;
    private LocalDateTime updatetime;
    private Integer state;
    private Integer is_deleted;
}
