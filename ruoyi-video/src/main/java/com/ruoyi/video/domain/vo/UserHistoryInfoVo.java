package com.ruoyi.video.domain.vo;

import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.video.domain.History;
import com.ruoyi.video.domain.Video;
import lombok.Data;


@Data
public class UserHistoryInfoVo extends History {
    /**
     * 视频对象
     */
    Video video;

    public UserHistoryInfoVo getUserHistoryInfoVo(History history) {
        BeanUtils.copyProperties(history,this);
        return this;
    }
}
