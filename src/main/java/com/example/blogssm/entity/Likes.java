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
 * @date 2024/04/08  20:16
 */
@Data
public class Likes implements Serializable {
    private Integer user_id;
    private Integer comment_id;
    private LocalDateTime like_time;
}