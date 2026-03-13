package com.ruoyi.video.mapper;

import com.ruoyi.video.domain.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper{
    List<Comment> getCommentByVideoId(Long id);

    int insertComment(Comment comment);

    int deleteComment(String commentId);


    int agreeComment(@Param("cid") Long cid,@Param("uid") Long uid);

    int disagreeComment(@Param("cid") Long cid,@Param("uid") Long uid);

    int isUserAgreeThisComment(@Param("cid") Long cid, @Param("uid") Long uid);

    int addComment(Comment comment);

    Comment selectById(Long id);
}
