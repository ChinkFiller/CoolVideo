package com.ruoyi.video.api.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
/* 视频解析请求结构 */
public class VideoInfoPackage {
    Long videoId;
    String videoName;
    Integer part;
    Integer totalPart;
}
