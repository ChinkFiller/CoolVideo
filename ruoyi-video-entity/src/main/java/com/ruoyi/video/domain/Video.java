package com.ruoyi.video.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 视频数据实体类
 * 用于存储视频相关信息，包括视频的基本信息、演员信息、统计数据等
 *
 * @author CodeGeeX
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("cv_video_info")
public class Video implements Serializable {

    /**
     * 视频ID，主键，自增
     */
    @Excel(name = "ID")
    @TableId(type = IdType.AUTO)
    Long id;

    /**
     * 视频名称
     */
    @Excel(name = "名称")
    String name;

    /**
     * 视频简介
     */
    @Excel(name = "简介")
    String ins;

    /**
     * 视频状态
     */
    @Excel(name = "视频状态，0已完结，1更新中，2停更")
    String state;

    /**
     * 公开时间
     * 使用DateTimeFormat和JsonFormat注解指定日期格式为yyyy-MM-dd HH:mm:ss
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间")
    LocalDateTime publicDate;

    /**
     * 视频领导者/导演
     */
    @Excel(name = "导演")
    String leader;

    /**
     * 演员
     */
    @Excel(name = "演员")
    String actor;

    /**
     * 封面的URL地址
     */
    @Excel(name = "封面")
    String imgUrl;

    /**
     * 视频部分/集数
     */
    @Excel(name = "分集")
    Integer part;

    /**
     * 点赞数
     */
    Integer likes;

    /**
     * 订阅数
     */
    Integer subscribe;

    /**
     * 周数，默认值为-1
     */
    Integer week = -1;


    @Excel(name = "视频分类标签")
    List<String> tags;

    String autoUpdateLock;
}
