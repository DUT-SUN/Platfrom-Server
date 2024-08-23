package com.example.blogssm;

import com.alibaba.fastjson.JSON;
import com.example.blogssm.entity.ArticleInfo;
import com.example.blogssm.entity.UserInfo;
import com.example.blogssm.entity.vo.BlogDoc;
import com.example.blogssm.service.ArticleService;
import com.example.blogssm.service.UserService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.util.List;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/03/27  14:17
 */
@SpringBootTest
public class BlogDocumentTest {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private UserService userService;
    private RestHighLevelClient client;
    @BeforeEach
    void setUp(){
        this.client=new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://39.105.29.212:9200")
        ));
    }
    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }
    @Test
    void testBulkRequest() throws IOException {
        //批量数据库查询数据
        List<ArticleInfo> articles=articleService.getlist();
        //1.创建request
        System.out.println(articles.size());
        BulkRequest request=new BulkRequest();
        //转换为文档类型HotelDoc
        for(ArticleInfo articleInfo:articles){
            UserInfo user=userService.getUserById(articleInfo.getUid());
            Integer id=articleInfo.getId();
            BlogDoc blogDoc=new BlogDoc(user,articleInfo);
            request.add(new IndexRequest("blog").id(blogDoc.getId().toString()).source(JSON.toJSONString(blogDoc), XContentType.JSON));
        }
        //2.设置request属性
        client.bulk(request, RequestOptions.DEFAULT);
    }
}
