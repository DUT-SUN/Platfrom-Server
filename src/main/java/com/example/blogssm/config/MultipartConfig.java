package com.example.blogssm.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;
import java.io.File;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/04/04  14:41
 */
//@Configuration
//public class MultipartConfig {
//
//    /**
//     * 文件上传临时路径,不加就会默认在Tomcat启动的文件上传的临时路径，但是由于那个文件自动被删除就会导致程序找不到删除的文件报错
//     */
//    @Bean
//    MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        String location = System.getProperty("user.dir") + "/data/upload/tmp";
//        File tmpFile = new File(location);
//        if (!tmpFile.exists()) {
//            tmpFile.mkdirs();
//        }
//        factory.setLocation(location);
//        return factory.createMultipartConfig();
//    }
//}