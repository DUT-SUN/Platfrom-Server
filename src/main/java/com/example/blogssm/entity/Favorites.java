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
public class Favorites implements Serializable {
    private Integer user_id;
    private Integer article_id;
    private LocalDateTime favorite_time;
}