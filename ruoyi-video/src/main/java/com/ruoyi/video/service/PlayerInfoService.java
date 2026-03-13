package com.ruoyi.video.service;

import com.ruoyi.video.domain.vo.PlayerInfoVo;

/** 播放器配置服务 **/
public interface PlayerInfoService {
    // 获取一个视频的播放量
    Integer getPlayCount(Long id);

    // 增加一个视频的播放量
    void addPlayCount(Long id);

    // 获取一个视频的播放信息
    PlayerInfoVo getPlayerInfo(Long id);
}
