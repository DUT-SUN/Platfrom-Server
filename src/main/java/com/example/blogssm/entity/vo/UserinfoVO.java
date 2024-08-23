package com.example.blogssm.entity.vo;

import com.example.blogssm.entity.UserInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2023/04/21  18:32
 */

@Data
public class UserinfoVO extends UserInfo implements Serializable {
    private Integer articleCount;
}
