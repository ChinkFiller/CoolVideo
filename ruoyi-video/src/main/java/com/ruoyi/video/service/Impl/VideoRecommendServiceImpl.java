package com.ruoyi.video.service.Impl;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.video.domain.Video;
import com.ruoyi.video.domain.vo.VideoInfoVo;
import com.ruoyi.video.mapper.VideoMapper;
import com.ruoyi.video.service.VideoRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VideoRecommendServiceImpl implements VideoRecommendService {

    @Autowired
    VideoMapper videoMapper;

    @Override
    @Cacheable(value = "videoRecommendCache", key = "#vid",sync = true)
    public List<VideoInfoVo> getRecommendList(Long vid) {
        List<Video> v = videoMapper.getRecommendVideo(vid);
        List<VideoInfoVo> vInfoVos = new ArrayList<>();
        if (SecurityUtils.getUserId()!=null){
            v.forEach(item->{
                VideoInfoVo infoVo=new VideoInfoVo().getVideoInfoVo(item);
                infoVo.setIsSubscribe(videoMapper.isUserSubscribeThisVideo(SecurityUtils.getUserId(),item.getId()));
                infoVo.setIsLike(videoMapper.isUserLikeThisVideo(SecurityUtils.getUserId(),item.getId()));
                vInfoVos.add(infoVo);
            });
        }
        return vInfoVos;
    }
}
