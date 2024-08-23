package com.example.blogssm.entity.vo;

import com.example.blogssm.entity.CommentInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/04/11  11:58
 */
@Data
@NoArgsConstructor
public class CommentinfoVO extends CommentInfo implements Serializable {
    private String username;
    private String avater;
    private String address;
}
