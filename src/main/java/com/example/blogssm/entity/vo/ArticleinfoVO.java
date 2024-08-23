package com.example.blogssm.entity.vo;

import com.example.blogssm.entity.ArticleInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/04/08  18:39
 */
@Data
@NoArgsConstructor
public class ArticleinfoVO extends ArticleInfo implements Serializable {
    private String username;
    public ArticleinfoVO(ArticleInfo articleInfo) {
        super(articleInfo.getId(), articleInfo.getTitle(), articleInfo.getDescription(), articleInfo.getAvatar(), articleInfo.getContent(), articleInfo.getCreatetime(), articleInfo.getUpdatetime(), articleInfo.getUid(), articleInfo.getState(), articleInfo.getStar(), articleInfo.getRcount(), articleInfo.getFavorite(), articleInfo.getComment(), articleInfo.getType());
        // 如果 ArticleInfo 类中有其他字段，你也需要在这里添加它们
    }
}
