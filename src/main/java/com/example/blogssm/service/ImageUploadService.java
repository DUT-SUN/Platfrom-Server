package com.example.blogssm.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/04/06  20:17
 */
@Service
public class ImageUploadService {

    private Path fileStoragePath;
    @PostConstruct
    public void init() {
        this.fileStoragePath = Paths.get("D:/work/").toAbsolutePath().normalize();
        try {
            Files.createDirectories(fileStoragePath);
        } catch (IOException e) {
            throw new RuntimeException("Issue in creating file directory");
        }
    }
    public String uploadImage(@RequestParam("avater") MultipartFile file, @RequestHeader("Authorization") String token){
        String fileName = file.getOriginalFilename();
        String newFileName = file.getName() + "-" + System.currentTimeMillis() + "." + fileName.split("\\.")[1];
        Path filePath = fileStoragePath.resolve(newFileName);
//        System.out.println("path:"+filePath);
        InputStream inputStream = null;
        File tempFile = null;
        try {
            inputStream = file.getInputStream();
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            tempFile = filePath.toFile();
        } catch (IOException e) {
            throw new RuntimeException("Issue in storing the file", e);
        } finally {
//            if (tempFile != null) {
//                tempFile.deleteOnExit(); // 删除临时文件，测试的时候重启项目自动删除
//            }
            if (inputStream != null) {
                try {
                    inputStream.close(); // 关闭临时文件的输入流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String fileRelativePath = "/images/" + newFileName;
        String path="http://localhost:8080" + fileRelativePath;
        return path;
    }
}
