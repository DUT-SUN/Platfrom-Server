package com.example.blogssm.mapper;

import com.example.blogssm.entity.ArticleInfo;
import com.example.blogssm.service.ArticleService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2023/04/21  18:23
 */
@Mapper
public interface ArticleMapper {
    int getArtCountByUid(@Param("uid") Integer uid);
    List<ArticleInfo> getMylist(@Param("uid") Integer uid);
    int del(@Param("id")Integer id,@Param(("uid"))Integer uid);

    ArticleInfo getDetail(@Param("id")Integer id);
    List <Integer>getArtIdsByUid(Integer uid);
    int incrrcount(@Param("id")Integer id);
    int add(ArticleInfo articleInfo);
    // 增加评论
    void increaseComment(int id);

    // 减少评论
    void decreaseComment(int id);
    List<ArticleInfo>getListByPage(@Param("psize")Integer psize,@Param("offsize") Integer offsize);

    List<ArticleInfo> getlist();

    int insertLike(Integer userId,Integer articleId);
    int updateArticleStar(Integer userId,Integer articleId);

    void updateArticleFavorite(Integer id, Integer articleId);

    void insertFavorite(Integer id, Integer articleId);

    List<Integer> getlikeList(Integer id);

    List<Integer> getfavoriteList(Integer id);

    void downArticleStar(Integer userId, Integer articleId);

    void delLike(Integer userId, Integer articleId);

    void cancelArticleFavorite(Integer id, Integer articleId);

    void removeFavorite(Integer id, Integer articleId);
    int update(Integer aid, ArticleInfo articleinfo);

    List<String> getAllArticleTypes();
}
