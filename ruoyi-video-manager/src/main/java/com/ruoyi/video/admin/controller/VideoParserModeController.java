package com.ruoyi.video.admin.controller;


import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.plugin.PluginLoader;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.video.admin.domain.vo.ParserModeVo;
import com.ruoyi.video.api.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/parser/mode")
public class VideoParserModeController extends BaseController {

    @Autowired
    ISysConfigService configService;

    @Autowired
    PluginLoader pluginLoader;

    private Map<String,String> getIndex(){
        Map<String,String> data=new HashMap<>();
        // 加入默认的Oss解析模式
        data.put("oss","通过Oss存储解析");
        pluginLoader.getPluginInfosByClass(PlayerService.class).forEach(plugin->{
            data.put(plugin.getPluginId(),plugin.getDescription());
        });
        return data;
    }

    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('system:parser:list')")
    public AjaxResult getParserModeList(){
        List<String> data=JSONArray.parseArray(configService.selectConfigByKey("video.parser.list"),String.class);
        Map<String,String> parserIndex=getIndex();
        List<Map<String,String>> result=new ArrayList<>();

        // 添加详细信息
        for(String parser:data){
            Map<String,String> map=new HashMap<>();
            map.put("parser",parser);
            map.put("description",parserIndex.get(parser));
            parserIndex.remove(parser);
            result.add(map);
        }

        // 添加剩余的解析器
        parserIndex.forEach((k,v)->{
            Map<String,String> map=new HashMap<>();
            map.put("parser",k);
            map.put("description",v);
            result.add(map);
        });

        return AjaxResult.success(result);
    }

    @PutMapping
    @PreAuthorize("@ss.hasPermi('system:parser:set')")
    public AjaxResult setParserMode(@RequestBody ParserModeVo vo){
        // 获取初始配置
        SysConfig config=configService.selectFullConfigByKey("video.parser.list");

        // 设置新的配置
        config.setConfigValue(JSONArray.toJSONString(vo.getParserModeList()));

        // 回写数据库
        return toAjax(configService.updateConfig(config));
    }
}
