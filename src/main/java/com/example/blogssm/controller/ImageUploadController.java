package com.example.blogssm.controller;

import com.example.blogssm.common.AjaxResult;
import com.example.blogssm.common.JwtUtils;
import com.example.blogssm.service.ImageUploadService;
import com.example.blogssm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

@RestController
@RequestMapping()
public class ImageUploadController {


    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;
    @Autowired
    private ImageUploadService imageUploadService;

    @PostMapping("/upload")
    public  AjaxResult uploadFile(@RequestParam("avater") MultipartFile file, @RequestHeader("Authorization") String token) {
        String path=imageUploadService.uploadImage(file,token);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("filepath",path);
        userService.UploadAvater(jwtUtils.getClaimsByToken(token).getSubject(),path);
        return AjaxResult.success(200,"图片上传成功",responseBody);
    }
}
