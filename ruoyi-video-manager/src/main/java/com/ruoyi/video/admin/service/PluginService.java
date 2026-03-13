package com.ruoyi.video.admin.service;

public interface PluginService {

     /**
      * 下载插件
      * @param url 插件url
      * @return 插件路径
      */
    String downloadAndSavePlugin(String url);
}
