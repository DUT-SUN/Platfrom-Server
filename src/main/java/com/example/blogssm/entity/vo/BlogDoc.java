package com.example.blogssm.entity.vo;

import com.example.blogssm.entity.ArticleInfo;
import com.example.blogssm.entity.UserInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/03/27  13:43
 */
@Data
@NoArgsConstructor
public class BlogDoc implements Serializable {
    private Integer id;
    private String username;
    private String title;
    private String content;
    private String description;
    private String avatar;
    private boolean isAD;
    private Integer uid;
    private Integer rcount;
    private Integer state;
    private Integer star;
    private Integer favorite;
    private Integer comment;
    private String type;
    private LocalDateTime createtime;
    private List<String> suggestion;
    public BlogDoc(UserInfo user, ArticleInfo article){
        this.id=article.getId();
        this.uid=user.getId();
        this.username=user.getUsername();
        this.title=article.getTitle();
        this.content=article.getContent();
        this.rcount=article.getRcount();
        this.favorite=article.getFavorite();
        this.comment=article.getComment();
        this.state =article.getState();
        this.description=article.getDescription();
        this.avatar=article.getAvatar();
        this.star=article.getStar();
        this.type=article.getType();
        this.createtime=article.getCreatetime();
       if(this.title.contains("|")){
           String[]arr=this.title.split("|");
           this.suggestion=new ArrayList<>();
           this.suggestion.add(this.title);
           Collections.addAll(suggestion,arr);
       }else if(this.title.contains("、")){
           String[]arr=this.title.split("、");
           this.suggestion=new ArrayList<>();
           this.suggestion.add(this.title);
           Collections.addAll(suggestion,arr);
       }else{
           this.suggestion= Arrays.asList(this.username,this.title);
       }
    }
}
