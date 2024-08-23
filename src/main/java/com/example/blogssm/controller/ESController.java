package com.example.blogssm.controller;

import com.example.blogssm.entity.PageResult;
import com.example.blogssm.entity.RequestParams;
import com.example.blogssm.service.ESearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/03/28  12:05
 */
@RestController
@RequestMapping("/blog")
public class ESController {
    @Autowired
    private ESearchService eSearchService;
    @PostMapping("/filters")
    public PageResult dataFilter(@RequestBody RequestParams params) throws IOException {
        System.out.println(params);
        return eSearchService.filters(params);
    }
    @GetMapping("/tags/{keyword}")
    public PageResult getTagList(@PathVariable String keyword) throws IOException {
        System.out.println(keyword);
        return eSearchService.getTagList(keyword);
    }
    @GetMapping("/suggestion")
    public List<String>getSuggestions(@RequestParam("key")String prefix){
//        System.out.println(prefix);
        return eSearchService.getSuggestions(prefix);
    };
}
