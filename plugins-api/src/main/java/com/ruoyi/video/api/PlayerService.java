package com.ruoyi.video.api;


import com.ruoyi.video.api.entity.VideoInfoPackage;
import com.ruoyi.video.api.entity.VideoParsedInfo;
import org.pf4j.ExtensionPoint;


/** 播放器解析接口 **/
public interface PlayerService extends ExtensionPoint {
    /** 获取视频的播放地址 **/
    VideoParsedInfo getVideoUrl(VideoInfoPackage videoInfoPackage);
}
