package com.ruoyi.video.mapper;

import com.ruoyi.video.domain.Banner;
import com.ruoyi.video.domain.FastFunction;
import com.ruoyi.video.domain.Video;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface IndexMapper {
    List<Banner> getBanner();
    List<Video> getChineseComic();
    List<Video> getHots();
    List<Video> getLast();

    List<FastFunction> getFastFunction();

    List<Map<String,Object>> getIndexNotice();

    List<Map<String,Object>> getPopNotice();
}
