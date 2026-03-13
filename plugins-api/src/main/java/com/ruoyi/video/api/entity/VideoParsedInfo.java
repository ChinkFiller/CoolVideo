package com.ruoyi.video.api.entity;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
/* 视频地址解析结果结构体 **/
public class VideoParsedInfo {
    @NonNull
    String url;
    String videoType;
    @NonNull
    Integer expireTime;
}
