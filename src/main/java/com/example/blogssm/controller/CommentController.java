package com.example.blogssm.controller;

import com.example.blogssm.common.AjaxResult;
import com.example.blogssm.common.RedisCache;
import com.example.blogssm.common.UserCacheUtils;
import com.example.blogssm.constants.Const;
import com.example.blogssm.entity.CommentInfo;
import com.example.blogssm.entity.CommentRequestParams;
import com.example.blogssm.entity.UserInfo;
import com.example.blogssm.entity.vo.CommentinfoVO;
import com.example.blogssm.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/04/09  15:54
 */
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private UserCacheUtils userCacheUtils;
    @Autowired
    private CommentService commentService;

    /**
     * 通过aid请求文章相关的所有评论信息
     * @param token
     * @param aid
     * @return
     */
//    @GetMapping("/getList/{aid}")
//    public AjaxResult getList(@RequestHeader("Authorization") String token,@PathVariable Integer aid) {
//        Integer id = redisCache.hget(token, Const.USER_ID);
//        if (id == null) {
//            UserInfo user = userCacheUtils.RESetUserCache(token);
//            id=user.getId();
//        }
//
//    }
    @PostMapping("/add")
    public AjaxResult AddComment(@RequestHeader("Authorization") String token, @RequestBody CommentRequestParams params) {
        Integer id = redisCache.hget(token, Const.USER_ID);
        if (id == null) {
            UserInfo user = userCacheUtils.RESetUserCache(token);
            id=user.getId();
        }
            params.setUid(id);
//        System.out.println(params.getAid());
//        System.out.println(params.getContent());
//        System.out.println(params.getReply_id());
//        System.out.println(params.getRoot_id());
//        System.out.println(params.getComment_id());
        //现在拿到了用户id，下一步就是去调用service存储数据到数据库中
        int flag=commentService.AddComment(params);
//        System.out.println(flag);
        if(flag==1){
            return AjaxResult.success(200,"评论成功！",params);
        }else{
            return AjaxResult.fail(500,"插入评论错误");
        }
    }

    /**
     * 获取指定文章的评论列表
     * @param aid
     * @return
     */
    @GetMapping("/list/{aid}")
    public AjaxResult getCommentList(@PathVariable Integer aid) throws JSONException {
        List <CommentinfoVO> commentList=commentService.getCommentList(aid);
        //数据预处理
        for (CommentinfoVO comment : commentList) {
            String address = comment.getAddress();
            JSONObject addressJson = new JSONObject(address);
            String nation = addressJson.getJSONObject("ad_info").getString("nation");
            String province = addressJson.getJSONObject("ad_info").getString("province");
            if ("中国".equals(nation)) {
                // 如果是中国，去掉"省"字
                province = province.replace("省", "");
                comment.setAddress(province);
            } else {
                // 如果是其他国家，只保留国家名称
                comment.setAddress(nation);
            }

        }
        return AjaxResult.success(200,"评论列表获取成功",commentList);
    }

    /**
     * 删除指定id的评论，验证身份
     * @param comment_id
     * @return
     */
    @DeleteMapping("/{aid}/{comment_id}")
    public AjaxResult deleteComment(@RequestHeader("Authorization") String token, @PathVariable Integer aid, @PathVariable String comment_id){
        System.out.println(comment_id);
        System.out.println(aid);
        Integer id = redisCache.hget(token, Const.USER_ID);
        if (id == null) {
            UserInfo user = userCacheUtils.RESetUserCache(token);
            id=user.getId();
        }
        //查询是不是这个用户的评论
        List<String> commentInfos=commentService.getCommentIDListById(aid,id);
        System.out.println(commentInfos);
        if(commentInfos.contains(comment_id)){
            if(commentService.deleteCommentById(aid,comment_id)==1)
            return AjaxResult.success(200,"删除评论成功",comment_id);
            else
                return AjaxResult.fail(500,"数据库错误");
        }else{
            return AjaxResult.fail(-1,"恶意删除他人评论错误");
        }

    }
}
