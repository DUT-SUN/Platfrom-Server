package com.example.blogssm.mq;

import com.example.blogssm.constants.MqConstant;
import com.example.blogssm.service.ArticleService;
import com.example.blogssm.service.ESearchService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/03/28  18:09
 */
@Component
public class  BlogListener {
    @Autowired
    private ESearchService eSearchService;
    @RabbitListener(queues = MqConstant.BLOG_ARTICLE_INSERT_QUEUE)
    public void InsertorUpdate(Integer id) throws InterruptedException {
//        System.out.println(111111);
        //为了让数据库更新先执行完
        Thread.sleep(1000);
        eSearchService.insertById(id);
    }
    @RabbitListener(queues = MqConstant.BLOG_ARTICLE_DELETE_QUEUE)
    public void Delete(Integer id) throws InterruptedException {
        Thread.sleep(1000);
        eSearchService.deleteById(id);
    }
}
