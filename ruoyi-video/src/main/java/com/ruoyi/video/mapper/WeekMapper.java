package com.ruoyi.video.mapper;



import com.ruoyi.video.domain.Video;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WeekMapper{
    List<Video> getWeekList();
}
