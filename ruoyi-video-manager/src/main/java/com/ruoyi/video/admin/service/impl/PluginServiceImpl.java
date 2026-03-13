package com.ruoyi.video.admin.service.impl;


import com.ruoyi.common.enums.ResultCodes;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.video.admin.service.PluginService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;

@Service
public class PluginServiceImpl implements PluginService {

    @Override
    public String downloadAndSavePlugin(String url) {
        try {
            // 下载插件
            byte[] data = HttpUtils.getContent(url);
            String fileName = UUID.getUUIDWithOutLine()+".jar";
            //设置为随机的UUID名称文件并保存在/plugins目录下
            File downloadFile = new File("/plugins/"+fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(downloadFile);
            if (data != null) {
                fileOutputStream.write(data);
            }
            fileOutputStream.close();
            return fileName;
        }catch (Exception e){
            throw new ServiceException(ResultCodes.PLUGIN_DOWNLOAD_ERROR);
        }
    }
}
