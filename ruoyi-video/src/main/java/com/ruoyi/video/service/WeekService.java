package com.ruoyi.video.service;




import com.ruoyi.video.domain.Video;

import java.util.List;
import java.util.Map;

public interface WeekService {
    Map<String,List<Video>> getWeekVideo();
}
