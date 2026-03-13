package com.ruoyi.video.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ruoyi.common.utils.finder.VideoInfoFinder;
import com.ruoyi.video.admin.mapper.UpdaterMapper;
import com.ruoyi.video.admin.service.VideoUpdateService;
import com.ruoyi.video.domain.Video;
import com.ruoyi.video.domain.WeekTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Service("updater")
public class VideoUpdateServiceImpl implements VideoUpdateService {

    @Autowired
    private UpdaterMapper updaterMapper;

    @Autowired
    private VideoInfoFinder finder;

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());


    private Long updateOneVideoInfo(Integer id){
        Map<String,Object> data=finder.getVideoPrimaryIns(id);
        String lockState = updaterMapper.getVideoLockState(data.get("name").toString());
        // 已完结状态，不进行更新
        if (data.get("state").equals("0")){return null;}
        // 如果设置了更新锁的数据，不进行更新
        if (lockState!=null){
            if (lockState.equals("1")){return null;}
        }
        Video video = MAPPER.convertValue(data, Video.class);
        updaterMapper.updateVideoData(video);
        return video.getId();
    }

    /**
     * 启动更新服务
     */
    @Override
    public void boot() {
        List<WeekTable> updatedVideoIds = new ArrayList<>();

        try {
            List<List<Integer>> weekData = finder.getWeekData();
            AtomicInteger weekDay = new AtomicInteger();

            for (List<Integer> perDay : weekData) {
                for (Integer id : perDay) {
                    try {
                        Long updatedId = updateOneVideoInfo(id);
                        if (updatedId != null) {
                            updaterMapper.updateWeekData(updatedId, weekDay.get());
                            // 添加到已更新列表中
                            updatedVideoIds.add(
                                    WeekTable.builder()
                                            .week(weekDay.get())
                                            .vid(updatedId)
                                            .build()
                            );
                        }
                    } catch (Exception e) {
                        // 写入日志
                        updaterMapper.setErrorLog("更新视频ID:" + id + "失败");
                    }
                }
                weekDay.set(weekDay.get() + 1);
            }
            weekData.clear();

            // 清除未更新的视频
            updaterMapper.clearOldWeekData(updatedVideoIds);

            // 设置更更新日志
            updaterMapper.setWeekUpdateLog(updatedVideoIds.size());
            updaterMapper.setUpdateLog("所有资源更新完毕！");

        } catch (Exception e) {
            updaterMapper.setErrorLog("更新服务出现错误:" + e.getMessage());
            throw new RuntimeException(e);

        }

    }
}
