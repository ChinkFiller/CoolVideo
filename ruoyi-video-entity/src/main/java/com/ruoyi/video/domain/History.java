package com.ruoyi.video.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 历史记录实体类
 * 用于存储用户观看历史记录信息
 */
@TableName("cv_user_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History implements Serializable {
    /**
     * 记录ID
     * 主键，自增类型
     */
    @TableId(type = IdType.AUTO)
    Long id;

    /**
     * 用户ID
     * 关联到用户表的主键
     */
    Long uid;

    /**
     * 更新时间
     * 记录最后更新的时间点
     */
    LocalDateTime updateTime;

    /**
     * 视频ID
     * 关联到视频表的主键
     */
    Long vid;

    /**
     * 视频分集
     * 用于标识视频的分集号
     */
    Integer part;

    /**
     * 观看时间
     * 单位：秒，记录用户观看的时长
     */
    Integer watchTime;
}
