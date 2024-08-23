package com.example.blogssm.service;

import com.example.blogssm.entity.CommentInfo;
import com.example.blogssm.entity.CommentRequestParams;
import com.example.blogssm.entity.vo.CommentinfoVO;
import com.example.blogssm.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/04/09  15:54
 */
@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ArticleService articleService;
//    private Integer aid;
//    private Integer uid;
//    private String content;
//    private Integer likes;//点赞数
//    private Integer state;//为后续的发送死信交换机做准备
    public int AddComment(CommentRequestParams params) {
        Integer uid=params.getUid();
        Integer aid=params.getAid();
        String  content=params.getContent();
        Integer likes=params.getLikes();
        Integer state=params.getState();
        String comment_id=params.getComment_id();
        String root_id=params.getRoot_id();
        String reply_id=params.getReply_id();
        articleService.increaseComment(aid);
        return commentMapper.AddComment(uid,aid,content,likes,state,comment_id,reply_id,root_id);
    }

    public List<CommentinfoVO> getCommentList(Integer aid) {
        return commentMapper.getCommentList(aid);
    }

    public List<String> getCommentIDListById(Integer aid,Integer uid) {
        return commentMapper.getCommentIDListById(aid,uid);
    }

    public int deleteCommentById(Integer aid,String commentId) {
        articleService.decreaseComment(aid);
         return commentMapper.deleteCommentById(commentId);
    }
}
