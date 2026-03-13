package com.ruoyi.video.service;


import com.ruoyi.video.domain.Comment;
import com.ruoyi.video.domain.vo.CommentDataVo;

import java.util.List;

public interface CommentService {
    Comment getOneComment(Long id);

    List<CommentDataVo> getComment(Long id);

    boolean addComment(Comment comment);

    boolean agreeOrDisAgreeComment(Comment data);
}
