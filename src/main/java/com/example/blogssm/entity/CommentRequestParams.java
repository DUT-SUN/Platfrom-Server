package com.example.blogssm.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 功能描述
 * <p>这个是comment添加的请求体
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/04/09  15:59
 */
@Data
public class CommentRequestParams implements Serializable {
    private Integer aid;
    private Integer uid;
    private String root_id;
    private String comment_id;
    private String reply_id;
    private String content;
    private Integer likes;//点赞数
    private Integer state;//为后续的发送死信交换机做准备
}
