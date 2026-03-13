package com.ruoyi.common.utils.plugin;


import com.ruoyi.common.enums.ResultCodes;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.finder.VideoInfoFinder;
import com.ruoyi.common.utils.parser.MultiPluginParserLoader;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//插件加载器
@Component
public class PluginLoader {

    @Autowired
    PluginManager pluginManager;

    @Autowired
    VideoInfoFinder finder;

    @Autowired
    MultiPluginParserLoader parserLoader;

    /**
     * 获取第一个插件，如果插件不存在，则抛出异常
     * @param loadClass 加载类
     * @return 类的实例
     */
    public <T> T loadPluginByClass(Class<T> loadClass){
        List<T> classList=pluginManager.getExtensions(loadClass);

        //插件存在
        if (!classList.isEmpty()){
            //返回第一个插件
            return classList.get(0);
        }else{
            throw new ServiceException(ResultCodes.PLUGIN_NOT_FOUND);
        }
    }


    public <T> T loadPluginById(String pluginId,Class<T> loadClass){
        List<T> classList=pluginManager.getExtensions(loadClass,pluginId);

        //插件存在
        if (!classList.isEmpty()){
            //返回第一个插件
            return classList.get(0);
        }else{
            return null;
        }
    }

    public <T> List<PluginInfo> getPluginInfosByClass(Class<T> type){
        return pluginManager.getPlugins().stream()
                .map(wrapper -> {
                    List<T> extensions = pluginManager.getExtensions(type, wrapper.getPluginId());
                    if (extensions == null || extensions.isEmpty()) {
                        return null;
                    }

                    PluginDescriptor descriptor = wrapper.getDescriptor();
                    return PluginInfo.builder()
                            .pluginId(descriptor.getPluginId())
                            .version(descriptor.getVersion())
                            .provider(descriptor.getProvider())
                            .description(descriptor.getPluginDescription())
                            .state(wrapper.getPluginState() == PluginState.STARTED)
                            .path(wrapper.getPluginPath().toString())
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
    }



    private List<PluginInfo> listPlugins() {
        List<PluginInfo> result = new ArrayList<>();

        for (PluginWrapper wrapper : pluginManager.getPlugins()) {
            PluginDescriptor descriptor = wrapper.getDescriptor();
            result.add(PluginInfo.builder()
                    .pluginId(descriptor.getPluginId())
                    .version(descriptor.getVersion())
                    .provider(descriptor.getProvider())
                    .description(descriptor.getPluginDescription())
                    .state(wrapper.getPluginState().toString().equals("STARTED"))
                    .path(wrapper.getPluginPath().toString())
                    .build());
        }

        return result;
    }

    public List<PluginInfo> listPlugins(int size,int page) {
        //获取全部的数据
        List<PluginInfo> result = listPlugins();

        //分页
        int start=(page-1)*size;
        int end=Math.min(start+size,result.size());
        return result.subList(start,end);
    }

    public void reload() {
       finder.reload();
       parserLoader.reload();
    }


}
