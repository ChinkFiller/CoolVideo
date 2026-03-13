package com.ruoyi.video.service.Impl;


import com.github.pagehelper.PageHelper;
import com.ruoyi.video.domain.Video;
import com.ruoyi.video.mapper.WeekMapper;
import com.ruoyi.video.service.WeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeekServiceImpl implements WeekService {

    @Autowired
    WeekMapper weekMapper;

    @Override
    @Cacheable(value = "weekVideo",key = "'WeekTable'",sync = true)
    public Map<String,List<Video>> getWeekVideo() {
        // 清除分页缓存，系统有僵尸分页线程
        PageHelper.clearPage();
        List<Video> data=weekMapper.getWeekList();
        Map<String,List<Video>> back=new HashMap<>();
        for (Video item : data) {
            Integer week = item.getWeek();
            // 如果Map中还没有这个week的键，则创建一个新的列表
            back.computeIfAbsent(String.valueOf(week), k -> new ArrayList<>());
            // 将当前数据添加到对应week的列表中
            back.get(week.toString()).add(item);
        }
        return back;
    }
}
