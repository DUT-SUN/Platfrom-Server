package com.example.blogssm.common;

import com.example.blogssm.constants.Const;
import com.example.blogssm.entity.ArticleInfo;
import com.example.blogssm.entity.vo.ArticleinfoVO;
import com.example.blogssm.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 功能描述
 * <p>文章列表缓存写回
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/04/04  21:23
 */
@Component
public class ArticleCacheUtils {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ArticleService articleService;

    public Set<Integer> getUserArticles(Integer id) {
        return redisCache.getCacheSet("user:" + id + ":articles");
    }

    //    public static final String ARTICLE_DESCRIPTION="article_description";
//    public static final String ARTICLE_AVATAR="article_avatar";
    // 2. 传入id数据库写回缓存set接口和同时写回article接口
    public List<ArticleInfo> cacheUserArticles(Integer id) {
        //从数据库拿了所有文章数据
        List<ArticleInfo> articlelist = articleService.getMylist(id);
        Set<Integer> articles = new HashSet<>();
        String key = "user:" + id + ":articles";
        for (ArticleInfo article : articlelist) {
            Integer artId = article.getId();
            cacheArticle(artId);
            articles.add(artId);//为了给用户对应的Set添加文章id
        }
        //全部加入到缓存之后,set到缓存中
        redisCache.setCacheSet(key, articles);
        return articlelist;
    }

    // 3. 获取到了set直接通过set的每一个值去拿列表
    public List<ArticleinfoVO> getArticlesFromSet(Set<Integer> articleIds) {
        List<ArticleinfoVO> list = new ArrayList<>();
        for (Integer id : articleIds) {
            ArticleinfoVO article= getArticle(id);
                list.add(article);
        }
        // 返回articles列表
        return list;
    }
    //设置一个article对象
    public void cacheArticle(Integer artId) {
        ArticleinfoVO article = articleService.getDetail(artId);
        redisCache.hset("article:" + artId, Const.ARTICLE_TITLE, article.getTitle(), 3600);
        redisCache.hset("article:" + artId, Const.ARTICLE_COMMENT, article.getComment(), 3600);
        redisCache.hset("article:" + artId, Const.ARTICLE_FAVORITE, article.getFavorite(), 3600);
        redisCache.hset("article:" + artId, Const.ARTICLE_CONTENT, article.getContent(), 3600);
        redisCache.hset("article:" + artId, Const.ARTICLE_RCOUNT, article.getRcount(), 3600);
        redisCache.hset("article:" + artId, Const.ARTICLE_STATE, article.getState(), 3600);
        redisCache.hset("article:" + artId, Const.ARTICLE_AVATAR, article.getAvatar(), 3600);
        redisCache.hset("article:" + artId, Const.ARTICLE_DESCRIPTION, article.getDescription(), 3600);
        redisCache.hset("article:" + artId, Const.ARTICLE_STAR, article.getStar(), 3600);
        redisCache.hset("article:" + artId, Const.ARTICLE_TYPE, article.getType(), 3600);
        redisCache.hset("article:" + artId, Const.USER_NAME, article.getUsername(), 3600);
    }




    //获取一个article对象
    public ArticleinfoVO getArticle(Integer id) {
        List<Object> articleProps = new ArrayList<>();
        articleProps.add(Const.ARTICLE_TITLE);
        articleProps.add(Const.ARTICLE_COMMENT);
        articleProps.add(Const.ARTICLE_FAVORITE);
        articleProps.add(Const.ARTICLE_CONTENT);
        articleProps.add(Const.ARTICLE_RCOUNT);
        articleProps.add(Const.ARTICLE_STATE);
        articleProps.add(Const.ARTICLE_AVATAR);
        articleProps.add(Const.ARTICLE_DESCRIPTION);
        articleProps.add(Const.ARTICLE_STAR);
        articleProps.add(Const.ARTICLE_TYPE);
        articleProps.add(Const.USER_NAME);
        List<Object> values = redisCache.getMultiCacheMapValue("article:" + id, articleProps);
        if (values == null || values.stream().allMatch(Objects::isNull)) {

            ArticleinfoVO article = articleService.getDetail(id);
            //这个文章没有就应该写回缓存
            cacheArticle(id);
            return article;
        }
        ArticleinfoVO article = new ArticleinfoVO();
        // 从values列表中获取文章信息，并设置到article对象中
        article.setId(id);
        article.setTitle((String) values.get(0));
        article.setComment((Integer) values.get(1));
        article.setFavorite((Integer) values.get(2));
        article.setContent((String) values.get(3));
        article.setRcount((Integer) values.get(4));
        article.setState((Integer) values.get(5));
        article.setAvatar((String) values.get(6));
        article.setDescription((String) values.get(7));
        article.setStar((Integer) values.get(8));
        article.setType((String)(values.get(9)));
        article.setUsername((String) values.get(10));
        return article;
    }

}

//    public UserInfo getUser(String token){
//        //用户缓存命中了返回
//        List<Object> UserPropList = new ArrayList<>();
//        UserPropList.add(Const.USER_ID);
//        UserPropList.add(Const.USER_NAME);
//        UserPropList.add(Const.USER_AVATER);
//        UserPropList.add(Const.USER_STATE);
//        List<Object> values = redisCache.getMultiCacheMapValue(token, UserPropList);
//
//        // 创建一个新的UserInfo对象
//        UserInfo userInfo = new UserInfo();
//
//        // 从values列表中获取用户信息，并设置到userInfo对象中
//        userInfo.setId((Integer) values.get(0));
//        userInfo.setUsername((String) values.get(1));
//        userInfo.setAvater((String) values.get(2));
//        userInfo.setState((Integer) values.get(3));
//        // 返回userInfo对象
//        return userInfo;
//    }
//    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet)

//public <T> Set<T> getCacheSet(final String key)
//{
//    return redisTemplate.opsForSet().members(key);
//}

//public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys)
//{
//    return redisTemplate.opsForHash().multiGet(key, hKeys);
//}

//            redisCache.hset(token, Const.USER_NAME, user.getUsername(), 3600);

//}
