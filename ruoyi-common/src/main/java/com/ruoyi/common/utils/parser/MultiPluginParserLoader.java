package com.ruoyi.common.utils.parser;

import com.ruoyi.common.utils.plugin.PluginLoader;
import com.ruoyi.video.api.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MultiPluginParserLoader {

    @Autowired
    PluginLoader pluginLoader;

    @Autowired
    @Qualifier("ossParserService")
    PlayerService ossPlayerService;

    public static Map<String, PlayerService> parserIndex=new ConcurrentHashMap<>();


    @PostConstruct
    public void init(){
        // 放入默认的oss服务实现
        parserIndex.put("oss",ossPlayerService);
        // 放入插件解析器
        pluginLoader.getPluginInfosByClass(PlayerService.class).forEach(plugin->{
            // 放入启动的插件解析器
            if (plugin.getState()){
                parserIndex.put(plugin.getPluginId(),pluginLoader.loadPluginById(plugin.getPluginId(),PlayerService.class));
            }
        });
    }

    public void reload(){
        parserIndex.clear();
        init();
    }


    public Map<String, PlayerService> getPluginParserList(){
        return parserIndex;
    }

}
