package com.ruoyi.video.service;

import com.ruoyi.video.domain.vo.VideoInfoVo;

import java.util.List;

/**
 * 视频推荐服务
 */
public interface VideoRecommendService {

    List<VideoInfoVo> getRecommendList(Long id);
}
