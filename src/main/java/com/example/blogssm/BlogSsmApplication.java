package com.example.blogssm;

import com.example.blogssm.CanalClient.CanalClient;
import io.lettuce.core.ReadFrom;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//防止加入security的登录页面
@SpringBootApplication(exclude ={SecurityAutoConfiguration.class})
@EnableCaching//开启全局注解缓存
public class BlogSsmApplication {
//    @Autowired
//    private  CanalClient client;
//    private Thread canalThread;
    private static final Logger loginLogger = LogManager.getLogger("loginLogger");
    public static void main(String[] args) {
        SpringApplication.run(BlogSsmApplication.class, args);
        loginLogger.info("Application started.");
    }
//    @PostConstruct
//    public void init() {
//        canalThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                client.start();
//            }
//        });
//        canalThread.start();
//    }
//    @PreDestroy
//    public void cleanup() {
//        if (canalThread != null) {
//            canalThread.interrupt();
//            canalThread = null;
//        }
//    }
    //配置读写分离
    @Bean
    public LettuceClientConfigurationBuilderCustomizer clientConfigurationBuilderCustomizer() {
        return new LettuceClientConfigurationBuilderCustomizer() {
            @Override
            public void customize(LettuceClientConfiguration.LettuceClientConfigurationBuilder clientConfigurationBuilder) {
                clientConfigurationBuilder.readFrom(ReadFrom.REPLICA_PREFERRED);
            }
        };
    }

    @Bean
    public RestHighLevelClient client(){
        return
                new RestHighLevelClient(RestClient.builder(
                        HttpHost.create("http://39.105.29.212:9200")
                ));
    }
}
