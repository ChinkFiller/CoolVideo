package com.ruoyi.video.admin.mapper;


import com.ruoyi.video.domain.Video;
import com.ruoyi.video.domain.WeekTable;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UpdaterMapper {

    String getVideoLockState(String name);
    void updateVideoData(Video data);
    void updateWeekData(@Param("vid") Long vid, @Param("week") Integer week);
    void clearOldWeekData(List<WeekTable> list);

    void setWeekUpdateLog(@Param("count") Integer count);

    void setErrorLog(String message);

    void setUpdateLog(String message);

    void clearOldUpdateLog();
}
