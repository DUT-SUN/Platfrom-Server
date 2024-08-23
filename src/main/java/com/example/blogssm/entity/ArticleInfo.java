package com.example.blogssm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2023/05/15  12:02
 */
@Data
@NoArgsConstructor
public class ArticleInfo implements Serializable {
    private Integer id;
    private String title;
    private String description;
    private String avatar;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss",timezone = "GMT+8")
    private LocalDateTime createtime;
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss",timezone = "GMT+8")
    private LocalDateTime updatetime;
    private Integer uid;
    private Integer state;
    private Integer star;
    private Integer rcount;
    private Integer favorite;
    private Integer comment;
    private String type;
    public ArticleInfo(Integer id, String title, String description, String avatar, String content, LocalDateTime createtime, LocalDateTime updatetime, Integer uid, Integer state, Integer star, Integer rcount, Integer favorite, Integer comment, String type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.avatar = avatar;
        this.content = content;
        this.createtime = createtime;
        this.updatetime = updatetime;
        this.uid = uid;
        this.state = state;
        this.star = star;
        this.rcount = rcount;
        this.favorite = favorite;
        this.comment = comment;
        this.type = type;
    }

}