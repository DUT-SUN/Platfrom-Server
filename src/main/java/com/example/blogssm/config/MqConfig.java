package com.example.blogssm.config;

import com.example.blogssm.constants.MqConstant;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/03/28  18:32
 */
@Configuration
public class MqConfig {
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(MqConstant.BLOG_EXCHANGE,true,false);
    }

    @Bean
    public Queue insertUserQueue(){
        return new Queue(MqConstant.BLOG_USER_INSERT_QUEUE,true);
    }

    @Bean
    public Queue deleteUserQueue(){
        return new Queue(MqConstant.BLOG_USER_DELETE_QUEUE,true);
    }
    @Bean
    public Queue insertArticleQueue(){
        return new Queue(MqConstant.BLOG_ARTICLE_INSERT_QUEUE,true);
    }

    @Bean
    public Queue deleteArticleQueue(){
        return new Queue(MqConstant.BLOG_ARTICLE_DELETE_QUEUE,true);
    }

    @Bean
    public Binding insertUserQueueBinding(){
        return BindingBuilder.bind(insertUserQueue()).to(topicExchange()).with(MqConstant.BLOG_USER_INSERT_KEY);
    }



    @Bean
    public Binding deleteUserQueueBinding(){
        return BindingBuilder.bind(deleteUserQueue()).to(topicExchange()).with(MqConstant.BLOG_USER_DELETE_KEY);
    }
    @Bean
    public Binding deleteArticleQueueBinding(){
        return BindingBuilder.bind(deleteArticleQueue()).to(topicExchange()).with(MqConstant.BLOG_ARTICLE_DELETE_KEY);
    }

    @Bean
    public Binding insertArticleQueueBinding(){
        return BindingBuilder.bind(insertArticleQueue()).to(topicExchange()).with(MqConstant.BLOG_ARTICLE_INSERT_KEY);
    }
}
