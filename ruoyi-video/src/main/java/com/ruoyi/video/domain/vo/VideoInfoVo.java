package com.ruoyi.video.domain.vo;

import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.video.domain.Video;
import lombok.*;


@Data
@EqualsAndHashCode(callSuper = true)
public class VideoInfoVo extends Video {
    /**
     * 播放次数
     */
    Integer playCount;

    /**
     * 是否已订阅
     */
    Integer isSubscribe = 0;

    /**
     * 是否已点赞
     */
    Integer isLike = 0;

    public VideoInfoVo getVideoInfoVo(Video video) {
        BeanUtils.copyProperties(video, this);
        return this;
    }
}
