package com.ruoyi.video.domain.vo;


import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.video.domain.Comment;
import lombok.Data;


@Data
public class CommentDataVo extends Comment {
    /**
     * 用户是否点赞的标识，0表示未点赞，1表示已点赞
     */
    Integer isUserAgree=0;

    public CommentDataVo getCommentDataVo(Comment comment){
        BeanUtils.copyProperties(comment,this);
        return this;
    }
}
