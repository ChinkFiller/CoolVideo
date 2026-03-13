package com.ruoyi.video.api;

import org.pf4j.ExtensionPoint;

import java.util.Map;

public interface PluginSettingGUI extends ExtensionPoint {

    /** 获取插件配置的html数据 **/
    String getSettingPage();

    /** 提交插件配置数据 **/
    void submitSetting(Map<String,Object> setting);
}
