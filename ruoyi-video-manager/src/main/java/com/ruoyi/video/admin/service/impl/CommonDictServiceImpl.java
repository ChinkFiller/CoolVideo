package com.ruoyi.video.admin.service.impl;

import com.ruoyi.common.utils.plugin.PluginLoader;
import com.ruoyi.video.admin.mapper.CommonDictMapper;
import com.ruoyi.video.admin.service.CommonDictService;
import com.ruoyi.video.api.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CommonDictServiceImpl implements CommonDictService {

    @Autowired
    CommonDictMapper commonIndexMapper;

    @Autowired
    PluginLoader pluginLoader;

    @Override
    public List<Map<String, Object>> selectVideoInfoDict() {
        return commonIndexMapper.selectVideoIndex();
    }

    @Override
    public List<Map<String, Object>> selectUserInfoDict() {
        return commonIndexMapper.selectUserIndex();
    }
}
