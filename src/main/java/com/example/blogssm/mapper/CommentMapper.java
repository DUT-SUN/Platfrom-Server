package com.example.blogssm.mapper;

import com.example.blogssm.entity.CommentInfo;
import com.example.blogssm.entity.vo.CommentinfoVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/04/09  15:54
 */
@Mapper
public interface CommentMapper {
    List<CommentinfoVO> getCommentList(Integer aid) ;

    int AddComment(Integer uid, Integer aid, String content, Integer likes, Integer state, String comment_id, String reply_id, String root_id);

    List<String>getCommentIDListById(Integer aid,Integer uid);

    int deleteCommentById(String commentId);
}
