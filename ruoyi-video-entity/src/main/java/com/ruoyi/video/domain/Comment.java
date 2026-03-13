package com.ruoyi.video.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论实体类，用于存储评论相关信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment implements Serializable {
    /**
     * 评论ID，使用数据库自增策略
     */
    @TableId(type = IdType.AUTO)
    Long id;

    /**
     * 用户ID，关联发表评论的用户
     */
    Long uid;

    /**
     * 点赞数，默认值为0
     */
    Integer agrees=0;

    /**
     * 评论内容
     */
    @Excel(name = "用户评论内容")
    String content;

    /**
     * 关联的视频ID
     */
    Long vid;

    /**
     * 评论发表时间
     */
    LocalDateTime time;

    @Excel(name = "用户发布时IP")
    String remoteIp;

    @Excel(name = "用户发布时IP所在地区")
    String remoteLocation;

    SysUser userData;
}
