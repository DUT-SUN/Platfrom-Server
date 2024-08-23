package com.example.blogssm.service;

import com.example.blogssm.entity.ArticleInfo;
import com.example.blogssm.entity.vo.ArticleinfoVO;
import com.example.blogssm.mapper.ArticleMapper;
import com.example.blogssm.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2023/04/21  18:30
 */
@Service
public class ArticleService {
    @Autowired
    ArticleMapper articleMapper;
    @Autowired
    UserMapper userMapper;

    public void insertLike(Integer userId, Integer articleId) {
        articleMapper.insertLike(userId, articleId);
    }
    public void updateArticleStar(Integer userId, Integer articleId){
        articleMapper.updateArticleStar(userId, articleId);
    }
    public int getArtCountByUid(Integer uid){
        return articleMapper.getArtCountByUid(uid);
    }
    public List<ArticleInfo> getMylist(Integer uid){
        return articleMapper.getMylist(uid);
    }
    public int del(Integer id,Integer uid){
        return articleMapper.del(id,uid);
    }
    public ArticleinfoVO getDetail(Integer id){
        ArticleinfoVO articleInfo=new ArticleinfoVO(articleMapper.getDetail(id));
        articleInfo.setUsername(userMapper.getNameById(articleInfo.getUid()));
        return articleInfo;
    }

    public int incrrcount(Integer id){return articleMapper.incrrcount(id);}

    public int  add(ArticleInfo articleInfo){return articleMapper.add(articleInfo);}
    public int update(Integer aid,ArticleInfo articleinfo) {
        return articleMapper.update(aid,articleinfo);
    }
    public List<ArticleInfo>getListByPage(Integer psize, Integer offsize){return articleMapper.getListByPage(psize,offsize);}

    public List<ArticleInfo> getlist() {
        return articleMapper.getlist();
    }

    public void updateArticleFavorite(Integer id, Integer articleId) {
        articleMapper.updateArticleFavorite(id, articleId);
    }

    public void insertFavorite(Integer id, Integer articleId) {
        articleMapper.insertFavorite(id, articleId);
    }

    public List<Integer> getlikeList(Integer id) {
        return   articleMapper.getlikeList(id);
    }
    public List <Integer>getArtIdsByUid(Integer uid){
        return articleMapper.getArtIdsByUid(uid);
    };

    public List<Integer> getfavoriteList(Integer id) {
        return   articleMapper.getfavoriteList(id);
    }

    public void downArticleStar(Integer userId, Integer articleId) {
        articleMapper.downArticleStar(userId,articleId);
    }

    public void delLike(Integer userId, Integer articleId) {
        articleMapper.delLike(userId,articleId);
    }

    public void cancelArticleFavorite(Integer id, Integer articleId) {
        articleMapper.cancelArticleFavorite(id,articleId);
    }

    public void removeFavorite(Integer id, Integer articleId) {
        articleMapper.removeFavorite(id,articleId);
    }

    public List<String> getAllArticleTypes() {
        return articleMapper.getAllArticleTypes();
    }
    // 增加评论
    public void increaseComment(int id) {
        articleMapper.increaseComment(id);
    }

    // 减少评论
    public void decreaseComment(int id) {
        articleMapper.decreaseComment(id);
    }
}
