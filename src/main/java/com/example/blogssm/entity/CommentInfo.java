package com.example.blogssm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/03/28  22:09
 */
@Data
public class CommentInfo implements Serializable {
    private Integer id;
    private Integer uid;
    private Integer aid;
    private String root_id;
    private String comment_id;
    private String reply_id;
    private String content;
    private Integer likes;
    private Integer state;
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss",timezone = "GMT+8")
    private LocalDateTime createtime;
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss",timezone = "GMT+8")
    private LocalDateTime updatetime;
}

