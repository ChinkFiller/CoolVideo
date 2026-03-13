package com.ruoyi.video.admin.domain.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentProhibitForm implements Serializable {
    //拦截关键词
    String keyword;
    //禁止用户评论
    String userIds;
    //禁止视频评论
    String videoIds;
}
