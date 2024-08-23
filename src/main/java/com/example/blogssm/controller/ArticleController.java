package com.example.blogssm.controller;

import com.example.blogssm.common.AjaxResult;
import com.example.blogssm.common.ArticleCacheUtils;
import com.example.blogssm.common.RedisCache;
import com.example.blogssm.common.UserCacheUtils;
//import com.example.blogssm.common.UserSessionUtils;
import com.example.blogssm.constants.Const;
import com.example.blogssm.constants.MqConstant;
import com.example.blogssm.entity.ArticleInfo;
import com.example.blogssm.entity.ArticleRequestParams;
import com.example.blogssm.entity.UserInfo;
import com.example.blogssm.entity.vo.ArticleinfoVO;
import com.example.blogssm.service.ArticleService;
import com.example.blogssm.service.ImageUploadService;
import com.example.blogssm.service.UserService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.java2d.pipe.AAShapePipe;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2023/05/15  12:22
 */
@RestController
@RequestMapping("/art")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private UserCacheUtils userCacheUtils;
    @Autowired
    private ArticleCacheUtils articleCacheUtils;
    @Autowired
    private ImageUploadService imageUploadService;
    @GetMapping("/tagList")
    public AjaxResult getAllArticleTypes() {
        List<String> tagList = articleService.getAllArticleTypes();
        Set<String> allTypes = new HashSet<>();
        for(String type : tagList){
            if(type != null) {
                allTypes.addAll(Arrays.asList(type.split(",")));
            }
        }
        return AjaxResult.success(200,"获取标签列表成功",new ArrayList<>(allTypes));
    }

    @GetMapping("/likeList")
    public AjaxResult likeList(@RequestHeader("Authorization") String token){
        Integer id = redisCache.hget(token, Const.USER_ID);
        if (id == null) {
            UserInfo user = userCacheUtils.RESetUserCache(token);
            id=user.getId();
        }
        List<Integer>likeList= articleService.getlikeList(id);
        return AjaxResult.success(200,"获取点赞文章列表成功",likeList);
    }
    @GetMapping("/favoriteList")
    public AjaxResult favoriteList(@RequestHeader("Authorization") String token){
        Integer id = redisCache.hget(token, Const.USER_ID);
        if (id == null) {
            UserInfo user = userCacheUtils.RESetUserCache(token);
            id=user.getId();
        }
        List<Integer>favoriteList= articleService.getfavoriteList(id);
        return AjaxResult.success(200,"获取收藏文章列表成功",favoriteList);
    }
    @DeleteMapping("/{articleId}/like")
    @Transactional
    public AjaxResult delLikeArticle(@PathVariable Integer articleId,@RequestHeader("Authorization") String token) {
        System.out.println(articleId);
        //获取缓存的id
        Integer id = redisCache.hget(token, Const.USER_ID);
        if (id == null) {
//            System.out.println(11111);
            //重新加载用户信息回缓存
            UserInfo user = userCacheUtils.RESetUserCache(token);
            id=user.getId();
        }
//        System.out.println(id);
        try {
            articleService.downArticleStar(id,articleId);
            articleService.delLike(id,articleId);
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
            throw e;
        }
        return AjaxResult.success(200,"文章取消点赞成功",null);
    }
    /**
     * 点赞文章接口
     * @param articleId
     * @return
     */
    @GetMapping("/{articleId}/like")
    @Transactional
    public AjaxResult likeArticle(@PathVariable Integer articleId,@RequestHeader("Authorization") String token) {
//        System.out.println(articleId);
        //获取缓存的id
        Integer id = redisCache.hget(token, Const.USER_ID);
        if (id == null) {
//            System.out.println(11111);
            //重新加载用户信息回缓存
            UserInfo user = userCacheUtils.RESetUserCache(token);
            id=user.getId();
        }
        System.out.println(id);
        try {
            articleService.updateArticleStar(id,articleId);
            articleService.insertLike(id,articleId);
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
            throw e;
        }
        return AjaxResult.success(200,"文章点赞成功,这篇文章会被更多人看到！",null);
    }

    /**
     * 取消收藏文章接口
     * @param articleId
     * @param token
     * @return
     */
    @DeleteMapping("/{articleId}/favorite")
    @Transactional
    public AjaxResult delFavoriteArticle(@PathVariable Integer articleId, @RequestHeader("Authorization") String token) {
        // 获取缓存的id
        Integer id = redisCache.hget(token, Const.USER_ID);
        if (id == null) {
            // 重新加载用户信息回缓存
            UserInfo user = userCacheUtils.RESetUserCache(token);
            id = user.getId();
        }
        try {
            // 减少文章收藏数
            articleService.cancelArticleFavorite(id, articleId);
            // 取消收藏记录
            articleService.removeFavorite(id, articleId);
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
            // 重新抛出异常
            throw e;
        }
        return AjaxResult.success(200, "取消收藏成功", null);
    }

    /**
     * 收藏文章接口
     * @param articleId
     * @return
     */
    @GetMapping("/{articleId}/favorite")
    @Transactional
    public AjaxResult favoriteArticle(@PathVariable Integer articleId, @RequestHeader("Authorization") String token) {
        // 获取缓存的id
        Integer id = redisCache.hget(token, Const.USER_ID);
        if (id == null) {
            // 重新加载用户信息回缓存
            UserInfo user = userCacheUtils.RESetUserCache(token);
            id = user.getId();
        }
        try {
            // 更新文章收藏数
            articleService.updateArticleFavorite(id, articleId);
            // 插入收藏记录
            articleService.insertFavorite(id, articleId);
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
            // 重新抛出异常
            throw e;
        }
        return AjaxResult.success(200, "文章收藏成功，好的东西偷偷藏着！", null);
    }
    @DeleteMapping("/del")
    public AjaxResult deleteArt(@RequestHeader("Authorization") String token,@RequestParam("aid") Integer aid) {
        if (aid == null || aid <= 0) {
            return AjaxResult.fail(-1, "参数异常！");
        }
        Integer id = redisCache.hget(token, Const.USER_ID);
        if (id == null) {
            //重新加载用户信息回缓存
            UserInfo user = userCacheUtils.RESetUserCache(token);
            id=user.getId();
        }
        List<Integer>aids=articleService.getArtIdsByUid(id);
        if(aids.contains(aid)){
            articleService.del(aid,id);
            rabbitTemplate.convertAndSend(MqConstant.BLOG_EXCHANGE,MqConstant.BLOG_ARTICLE_DELETE_KEY, aid);
            return AjaxResult.success(200,"删除文章成功",aid);
        }else{
            return AjaxResult.fail(-1,"非法删除他人文章");
        }
    }
    @GetMapping("/channels")
    public AjaxResult getMyList() {
        // 创建一个channels列表
        List<Map<String, Object>> channels = new ArrayList<>();

        // 定义一些静态的频道数据
        String[][] staticChannels = {
                {"1", "React"},
                {"2", "JAVA"},
                {"3", "后端"},
                {"4", "前端"},
                {"5", "数据库"},
                {"6", "ES"},
                {"7", "Docker"},
        };

        // 将每个频道转换为一个Map，然后添加到channels列表中
        for (String[] channelData : staticChannels) {
            Map<String, Object> channel = new HashMap<>();
            channel.put("id", channelData[0]);
            channel.put("name", channelData[1]);
            channels.add(channel);
        }

        return AjaxResult.success(channels);
    }

    @RequestMapping("/mylist")
    public AjaxResult getMyList(@RequestHeader("Authorization") String token) {
        //获取缓存的id
        Integer id = redisCache.hget(token, Const.USER_ID);
//        System.out.println(id);
        //获取不到id，也就获取不到user id的list列表缓存
        if (id == null) {
//            System.out.println(11111);
            //重新加载用户信息回缓存
            UserInfo user = userCacheUtils.RESetUserCache(token);
            //拿数据库返回的id直接查询数据库
            List<ArticleInfo> list = articleService.getMylist(user.getId());
            List<ArticleinfoVO> voList = new ArrayList<>();
            for (ArticleInfo articleInfo : list) {
                ArticleinfoVO articleinfoVO = new ArticleinfoVO(articleInfo);
                articleinfoVO.setUsername(userService.getNameById(articleInfo.getUid()));
                voList.add(articleinfoVO);
            }
            return AjaxResult.success(200, "文章详情获取成功！", voList);
        }
        //id不为空，说明用户缓存是有的，但是用户列表缓存不一定有
        //根据用户id获取user:id:articles 的set集合看看有没有，如果没有就写回
        //所以需要1.传入id，获取用户set接口以及2.传入id数据库写回缓存set接口和同时写回article接口，我这个接口是查询用户文章的所有数据，所以需要全部写回
        //获取不到用户的列表缓存的值
        Set<Integer> articleIds = articleCacheUtils.getUserArticles(id);
//        System.out.println(articleIds);
        if (articleIds.size() == 0) {
//            System.out.println(22222);
            //这时候就选择写回,这时候直接返回数据库查询出来的list
            List<ArticleInfo>list=articleCacheUtils.cacheUserArticles(id);
                    List<ArticleinfoVO> voList = new ArrayList<>();
            for (ArticleInfo article : list) {
                ArticleinfoVO articleinfoVO = new ArticleinfoVO(article);
                articleinfoVO.setUsername(userService.getNameById(article.getUid()));
                voList.add(articleinfoVO);
            }
            return AjaxResult.success(200, "文章详情获取成功！", voList);
        }
//        System.out.println(333333);
        List<ArticleinfoVO>list=articleCacheUtils.getArticlesFromSet(articleIds);
        return AjaxResult.success(200, "文章详情获取成功！", list);
    }



    @RequestMapping("/detail")
    public AjaxResult getDetail(@RequestHeader("Authorization") String token,Integer id) {
        if (id == null || id <= 0) {
            return AjaxResult.fail(-1, "非法参数");
        }
        //获取用户id（不是文章id
        Integer userid = redisCache.hget(token, Const.USER_ID);
        //获取缓存数据，缓存数据为空的时候
        if (userid == null) {
            //写回并返回数据的用户信息
            UserInfo user = userCacheUtils.RESetUserCache(token);
            userid=user.getId();
        }
        //获取用户的数据列表
        Set<Integer>articles=articleCacheUtils.getUserArticles(userid);
        if(articles.size()==0){
            //说明没了,重新写回缓存并且返回文章列表
            List<ArticleInfo> articleInfos=articleCacheUtils.cacheUserArticles(userid);
            for(ArticleInfo articleInfo:articleInfos){
                //遍历找到
                if(articleInfo.getId()==id){
                    ArticleinfoVO articleinfoVO=new ArticleinfoVO(articleInfo);
                    articleinfoVO.setUsername(userService.getNameById(id));
                    return AjaxResult.success(200,"文章详情获取成功！",articleinfoVO);
                }
            }
            return AjaxResult.fail(-1,"无权访问别人文章！");
        }
        //确实是这个用户的文章
        if (articles.contains(id)) {
            ArticleinfoVO articleinfoVO=new ArticleinfoVO(articleCacheUtils.getArticle(id));
            String name= redisCache.hget(token, Const.USER_NAME);
            if(name==null){
                name=userService.getNameById(id);
            }
            articleinfoVO.setUsername(name);
            return AjaxResult.success(200,"获取文章详情成功",articleinfoVO);
        } else {
            // 如果不包含 id,那就去返回错误
            return AjaxResult.fail(-1,"无权访问别人文章！");
        }
    }

    @RequestMapping("/incr-rcount")
    public AjaxResult incrRcount(Integer id) {
        if (id != null && id > 0) {
            return AjaxResult.success(articleService.incrrcount(id));
        }
        return AjaxResult.fail(-1, "未知错误!");
    }

    @RequestMapping("/upload")
    public AjaxResult upload(@RequestHeader("Authorization") String token, @RequestParam("image") MultipartFile image) {

        String url = imageUploadService.uploadImage(image, token);
        Map<String, String> map = new HashMap<>();
        map.put("url", url);
        // 其他代码...
        return AjaxResult.success(200, "上传图片成功", map);
    }

    @RequestMapping("/add")
    public AjaxResult addArticle(@RequestHeader("Authorization") String token, @RequestBody ArticleRequestParams article) {
        System.out.println(article);
        String title = article.getTitle();
        String content = article.getContent();
        String description = article.getDescription();
        System.out.println(description);
        List<String> selectedTags = article.getSelectedTags();
        List<String> avatars = article.getAvatars();
//        System.out.println(title);
        //首先获取用户信息缓存
        Integer userid = redisCache.hget(token, Const.USER_ID);
        ArticleInfo articleInfo = new ArticleInfo();
        articleInfo.setTitle(title);
        articleInfo.setContent(content);
        //怎么将字符串数组转化成字符串存储
        if(avatars!=null) {
            String avatarsStr = String.join(",", avatars);
            articleInfo.setAvatar(avatarsStr);
        }
//        System.out.println(avatarsStr);
        articleInfo.setDescription(description);
        if(selectedTags!=null){
            String tagsStr = String.join(",",selectedTags);
            articleInfo.setType(tagsStr);
        }
        //用户id缓存未命中
        if (userid == null) {
            //写回并返回数据的用户信息
            UserInfo user = userCacheUtils.RESetUserCache(token);
            //拿到用户信息之后下一步我需要去插入文章数据到数据库中
            userid=user.getId();
        }
        //用户缓存命中了
        articleInfo.setUid(userid);
        articleService.add(articleInfo);
        Integer id = articleInfo.getId();
        System.out.println(id);
        // 发送请求同步ES
        rabbitTemplate.convertAndSend(MqConstant.BLOG_EXCHANGE,MqConstant.BLOG_ARTICLE_INSERT_KEY, id);
        return AjaxResult.success(200, "上传文章成功",id);
    }
    @RequestMapping("/update")
    public AjaxResult updateArticle(@RequestHeader("Authorization") String token, @RequestBody ArticleRequestParams article) {
        Integer userid = redisCache.hget(token, Const.USER_ID);
        if (userid == null) {
            UserInfo user = userCacheUtils.RESetUserCache(token);
            userid=user.getId();
        }
        Set<Integer>articleIds= articleCacheUtils.getUserArticles(userid);

        //文章缓存没有了，重新加载
        if (articleIds.size() == 0) {
            boolean flag=false;
            List<ArticleInfo> list = articleCacheUtils.cacheUserArticles(userid);
            for(ArticleInfo articleInfo:list){
                if(Objects.equals(articleInfo.getId(), article.getAid())){
                    flag=true;
                }
            }
            if(!flag){
                return AjaxResult.fail(-1,"非法修改他人文章");
            }
        }
        //不是这个文章的主人直接返回
        if(!articleCacheUtils.getUserArticles(userid).contains(article.getAid())){
            return AjaxResult.fail(-1,"非法修改他人文章");
        };

        //到这就说明确实是这篇文章的主人
        ArticleInfo articleInfo = new ArticleInfo();
        articleInfo.setUid(userid);
        String title = article.getTitle();
        String content = article.getContent();
        String description = article.getDescription();
        List<String> selectedTags = article.getSelectedTags();
        List<String> avatars = article.getAvatars();
//        System.out.println(userid);
//        System.out.println(title);
//        System.out.println(content);
//        System.out.println(description);
//        System.out.println(selectedTags);
//        System.out.println(avatars);

        //首先获取用户信息缓存
        articleInfo.setTitle(title);
        articleInfo.setContent(content);
        articleInfo.setDescription(description);
//        System.out.println(article.getAid());

        if(avatars!=null) {
            String avatarsStr = String.join(",", avatars);
            articleInfo.setAvatar(avatarsStr);
        }
        if(selectedTags!=null){
            String tagsStr = String.join(",",selectedTags);
            articleInfo.setType(tagsStr);
        }
        articleService.update(article.getAid(),articleInfo);
        return AjaxResult.success(200, "上传文章成功", articleInfo);
    }



    /**
     * @param pindex 当前页码，从1开始
     * @param psize  每页显示条数
     * @return com.example.blogssm.common.AjaxResult
     * @explain
     * @author DUT-SUN
     * @date 2023/5/29
     */
    @RequestMapping("listbypage")
    public AjaxResult getListByPage(Integer pindex, Integer psize) {
//        1.参数矫正
        if (pindex == null || pindex <= 1) {
            pindex = 1;
        }
        if (psize == null || psize <= 1) {
            psize = 2;
        }
        int offset = (pindex - 1) * psize;
        List<ArticleInfo> list = articleService.getListByPage(psize, offset);
        return AjaxResult.success(list);
    }
}
