package com.example.blogssm.constants;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/03/28  18:06
 */
public class MqConstant {
    //交换级
    public static final  String BLOG_EXCHANGE="blog.topic";
    //队列
    public static final String BLOG_USER_INSERT_QUEUE="blog.user.insert.queue";
    public static final String BLOG_USER_DELETE_QUEUE="blog.user.delete.queue";
    public static final String BLOG_ARTICLE_INSERT_QUEUE="blog.article.insert.queue";
    public static final String BLOG_ARTICLE_DELETE_QUEUE="blog.article.delete.queue";
    //routing key
    public static final String BLOG_USER_INSERT_KEY="blog.user.insert";
    public static final String BLOG_USER_DELETE_KEY="blog.user.delete";
    public static final String BLOG_ARTICLE_INSERT_KEY="blog.article.insert";
    public static final String BLOG_ARTICLE_DELETE_KEY="blog.article.delete";
}
