package com.example.blogssm;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.io.IOException;

import static com.example.blogssm.constants.Blogconstant.MappingTemplate;

//@SpringBootTest
class BlogSsmApplicationTests {

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
    //判断我的索引表还在不在
    @Test
    void ExistHotelIndex() throws IOException {
        GetIndexRequest getIndexRequest =new GetIndexRequest("blog"); ;
        Boolean flag=client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println(flag);
    }
    //删除索引表blog
    @Test
    void DeleteHotelIndex() throws IOException {
        //1.创建Request对象
        DeleteIndexRequest request=new DeleteIndexRequest("blog");
        client.indices().delete(request, RequestOptions.DEFAULT);
    }
}
