package com.example.blogssm.entity;

import com.example.blogssm.entity.vo.BlogDoc;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/03/28  17:00
 */
@Data
public class PageResult implements Serializable {
    private Long total;
    private List<BlogDoc> blogDocs;

    public PageResult(Long total, List<BlogDoc> blogDocs) {
        this.total = total;
        this.blogDocs = blogDocs;
    }
}
