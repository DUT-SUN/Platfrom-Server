package com.example.blogssm.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/03/28  12:14
 */
@Data
public class RequestParams implements Serializable {
    private String key;
    private Integer page;
    private Integer size;
}
