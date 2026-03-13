package com.ruoyi.video.domain.vo;


import com.ruoyi.video.domain.History;
import com.ruoyi.video.domain.Video;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerInfoVo implements Serializable {
    /**视频的ID**/
    Long vid;
    /**视频请求安全密钥**/
    String securityKey;
    /**视频的具体详情**/
    Video videoData;
    /**视频的历史记录**/
    History history;
}
