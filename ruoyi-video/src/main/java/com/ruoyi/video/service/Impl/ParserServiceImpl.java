package com.ruoyi.video.service.Impl;

import com.ruoyi.system.service.ISysOssClientService;
import com.ruoyi.system.service.ISysOssFileService;
import com.ruoyi.video.api.PlayerService;
import com.ruoyi.video.api.entity.VideoInfoPackage;
import com.ruoyi.video.api.entity.VideoParsedInfo;
import com.ruoyi.video.mapper.VideoOssParserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("ossParserService")
public class ParserServiceImpl implements PlayerService {

    @Autowired
    VideoOssParserMapper videoOssParserMapper;

    @Autowired
    ISysOssFileService ossFileService;

    @Autowired
    ISysOssClientService clientService;

    @Override
    public VideoParsedInfo getVideoUrl(VideoInfoPackage videoInfoPackage) {

        // 获取oss文件key
        String ossFileKey= videoOssParserMapper.selectVideoOssKey(videoInfoPackage.getVideoId(),videoInfoPackage.getPart(),clientService.getActiveConfig().getOssConfigId());
        if (ossFileKey==null){
            throw new RuntimeException("所解析视频不存在");
        }

        // 生成视频地址，4小时有效
        return VideoParsedInfo.builder()
                .url(ossFileService.generateDownloadUrl(ossFileKey,240))
                .videoType("mp4")
                .expireTime(240)
                .build();
    }
}
