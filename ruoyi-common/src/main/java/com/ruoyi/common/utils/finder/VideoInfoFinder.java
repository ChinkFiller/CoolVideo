package com.ruoyi.common.utils.finder;


import com.ruoyi.common.enums.ResultCodes;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.plugin.PluginLoader;
import com.ruoyi.video.api.VideoFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Component
public class VideoInfoFinder {
    @Autowired
    private PluginLoader pluginLoader;

    private static VideoFinder finder;

    @PostConstruct
    public void init() {
        try {
            finder = pluginLoader.loadPluginByClass(VideoFinder.class);
        }catch (Exception e){
            System.out.println("finder插件未安装，自动更新功能可能无法使用");
        }
    }

    public void reload(){
        try {
            finder = pluginLoader.loadPluginByClass(VideoFinder.class);
        }catch (Exception ignored){
        }
    }


    public List<List<Integer>> getWeekData() {
        try {
            return finder.getWeekData();
        }catch (Exception e){
            return null;
        }
    }


    public Map<String, Object> getVideoPrimaryIns(Integer id) {
        try {
            return finder.getVideoPrimaryIns(id);
        }catch (Exception e){
            return null;
        }
    }
}
