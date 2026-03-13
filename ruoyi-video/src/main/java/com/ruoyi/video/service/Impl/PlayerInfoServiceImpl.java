package com.ruoyi.video.service.Impl;

import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.video.domain.bo.PlayCountBo;
import com.ruoyi.video.domain.vo.PlayerInfoVo;
import com.ruoyi.video.domain.vo.VideoInfoVo;
import com.ruoyi.video.mapper.VideoMapper;
import com.ruoyi.video.service.HistoryService;
import com.ruoyi.video.service.PlayerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class PlayerInfoServiceImpl implements PlayerInfoService {

    @Autowired
    RedisCache redisService;

    @Autowired
    VideoMapper videoMapper;

    @Autowired
    HistoryService historyService;

    @Autowired
    PlayerInfoService playCountService;


    // 初始化加载播放量数据至redis缓存
    @PostConstruct
    public void init(){
        List<PlayCountBo> data=videoMapper.getAllVideoPlayCount();
        Map<String,Integer> cacheData=new HashMap<>();
        data.forEach(item->{
            cacheData.put(item.getId(),item.getPlayCount());
        });
        //将播放量数据加载到缓存
        redisService.setCacheMap("playCount",cacheData);
    }

    @Scheduled(fixedRate = 60*60*1000)
    @PreDestroy
    public void updatePlayCountJob() {
        List<PlayCountBo> data = new ArrayList<>();

        redisService.getCacheMap("playCount").forEach((k, v) -> {
            PlayCountBo a = new PlayCountBo();
            a.setId(k);
            a.setPlayCount(Integer.parseInt(v.toString()));
            data.add(a);
        });

        if (!data.isEmpty()) {
            videoMapper.updatePlayCount(data);
        }
    }


    @Override
    public Integer getPlayCount(Long id) {
        return redisService.getCacheMapValue("playCount",id.toString());
    }

    @Override
    public void addPlayCount(Long id) {
        redisService.hincr("playCount",id.toString(),1);
    }



    @Override
    @Transactional
    public PlayerInfoVo getPlayerInfo(Long id) {
        //获取视频信息
        VideoInfoVo infoVo=new VideoInfoVo().getVideoInfoVo(videoMapper.getVideo(id));

        //获取视频播放次数
        infoVo.setPlayCount(redisService.getCacheMapValue("playCount",id.toString()));

        String key=UUID.getUUIDWithOutLine();
        // 存储安全密钥，设置ttl为60秒
        redisService.setCacheObject(CacheConstants.PLAY_URL_KEY +key,id,30,TimeUnit.SECONDS);
        //加载用户点赞或者订阅
        infoVo.setIsLike(videoMapper.isUserLikeThisVideo(SecurityUtils.getUserId(),id));
        infoVo.setIsSubscribe(videoMapper.isUserSubscribeThisVideo(SecurityUtils.getUserId(),id));

        // 播放量增加1
        playCountService.addPlayCount(id);

        return PlayerInfoVo.builder()
                .vid(id)
                .securityKey(key)
                .videoData(infoVo)
                .history(historyService.getUserOneHistory(SecurityUtils.getUserId(),id))
                .build();
    }
}
