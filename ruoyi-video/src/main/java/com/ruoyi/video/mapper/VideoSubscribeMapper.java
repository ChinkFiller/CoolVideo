package com.ruoyi.video.mapper;

import com.ruoyi.video.domain.Video;

import java.util.List;
import java.util.Map;

public interface VideoSubscribeMapper {
   List<Video> selectUserSubscribeVideos(Long uid);


   void upsert(List<Map<String,String>> list);
}
