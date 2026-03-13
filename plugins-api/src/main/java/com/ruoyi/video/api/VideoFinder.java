package com.ruoyi.video.api;

import org.pf4j.ExtensionPoint;

import java.util.List;
import java.util.Map;

public interface VideoFinder extends ExtensionPoint {
    /**
     * 获取单个视频详情
     * @param id 视频ID
     * @return 详情数据
     */
    Map<String, Object> getVideoPrimaryIns(int id) throws Exception;

    /**
     * 获取周数据（按星期分类的视频ID列表）
     * @return 周数据
     */
    List<List<Integer>> getWeekData() throws Exception;
}
