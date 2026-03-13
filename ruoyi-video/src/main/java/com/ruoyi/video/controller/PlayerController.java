package com.ruoyi.video.controller;


import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.ResultCodes;
import com.ruoyi.common.utils.parser.MultiPluginParserLoader;
import com.ruoyi.common.utils.plugin.PluginLoader;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.video.api.entity.VideoInfoPackage;
import com.ruoyi.video.api.entity.VideoParsedInfo;
import com.ruoyi.video.domain.Video;
import com.ruoyi.video.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/player")
@Tag(name = "视频播放接口", description = "播放视频的接口")
public class PlayerController extends BaseController {

    @Autowired
    PluginLoader pluginLoader;

    @Autowired
    RedisCache redisService;

    @Autowired
    VideoService videoService;

    @Autowired
    ISysConfigService configService;

    @Autowired
    MultiPluginParserLoader  multiPluginParserLoader;



    @Anonymous
    @GetMapping("/video/play/url/{id}/{part}/{accessKey}.mp4")
    @Operation(summary = "通过获得视频id和选集获取播放外链，重定向获得")
    public AjaxResult getVideo(
            @PathVariable Long id,
            @PathVariable Integer part,
            @PathVariable String accessKey
    ){

        // 获取预期的访问key
        Long exceptID=redisService.getCacheObject(CacheConstants.PLAY_URL_KEY+accessKey);
        if (exceptID==null || !exceptID.equals(id)){
            throw new RuntimeException(ResultCodes.SIGN_ERROR.getMsg());
        }

        // 查询视频的完整信息
        Video videoInfo=videoService.getOneVideo(id);

        // 判断分集是否存在
        if (videoInfo.getPart()<part){
            throw new RuntimeException(ResultCodes.NOT_FOUND.getMsg());
        }

        // 获取解析顺序数组
        List<String> parserList = JSONArray.parseArray(configService.selectConfigByKey("video.parser.list"),String.class);

        // 构建解析信息的实体
        VideoInfoPackage videoInfoPackage=VideoInfoPackage.builder()
                .videoId(id)
                .videoName(videoInfo.getName())
                .part(part)
                .totalPart(videoInfo.getPart())
                .build();

        // 初始化返回信息
        VideoParsedInfo data = null;

        // 获取所有插件解析器列表
        Map<String, com.ruoyi.video.api.PlayerService> parserIndex=multiPluginParserLoader.getPluginParserList();

        // 遍历解析顺序数组，依次尝试解析
        for (String item : parserList) {
            try {
                // 插件外链缓存存在时，返回缓存的链接
                if (redisService.getCacheObject(item+":videoUrl:"+id+":"+part)!=null){
                    return success(Map.of("url",redisService.getCacheObject(item+":videoUrl:"+id+":"+part).toString()));
                }
                data = parserIndex.get(item).getVideoUrl(videoInfoPackage);
                // 不是本地的oss解析，是某一个插件的外链解析，进行缓存
                if (!item.equals("oss")){
                    redisService.setCacheObject(item+":videoUrl:"+id+":"+part, data.getUrl(), data.getExpireTime(), TimeUnit.MINUTES);
                }
                if (data != null) break; // 成功就跳出循环
            } catch (Exception e) {
                e.printStackTrace();
                data = null;
            }
        }

        if (data==null){
            // 解析器都失败，返回解析错误信息
            throw new RuntimeException("暂无可播放的路线");
        }

        return success(Map.of("url",data.getUrl()));
    }
}
