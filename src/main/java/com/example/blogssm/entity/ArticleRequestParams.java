package com.example.blogssm.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/04/06  21:16
 */
@Data
public class ArticleRequestParams implements Serializable {
    private Integer aid;
    private String title;
    private String content;
    private String description;
    private List<String>selectedTags;
    private List<String> avatars;
}

