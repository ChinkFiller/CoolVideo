package com.ruoyi.video.service.Impl;


import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.video.domain.History;
import com.ruoyi.video.domain.vo.UserHistoryInfoVo;
import com.ruoyi.video.mapper.HistoryMapper;
import com.ruoyi.video.mapper.VideoMapper;
import com.ruoyi.video.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 用户历史记录，采用redis缓冲加响应式落库和定时落库，使得服务支持高并发
@Service
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    HistoryMapper historyMapper;

    @Autowired
    VideoMapper videoMapper;

    @Autowired
    RedisCache redisService;

    private final String HISTORY_KEY="userHistoryActiveDataCache";

    // 初始化数据加载至Redis缓存
    @PostConstruct
    public void init(){
        List<History> data=historyMapper.getAllUserHistory();
        HashMap<String,String> cacheMap=new HashMap<>();
        for (History history : data) {
            // 缓存放入易改动数据
            cacheMap.put(history.getUid()+"::"+history.getVid(),history.getPart()+"::"+history.getWatchTime()+"::"+history.getUpdateTime());
        }
        redisService.setCacheMap(HISTORY_KEY,cacheMap);
    }


    @Scheduled(fixedRate = 60*60*1000)
    @PreDestroy
    public void flushHistoryToDB(){
        Map<String,String> cacheMap=redisService.getCacheMap(HISTORY_KEY);
        List<History> list=new ArrayList<>();
        for (Map.Entry<String, String> entry : cacheMap.entrySet()) {
            String[] data=entry.getValue().split("::");
            History history=new History();
            history.setUid(Long.parseLong(entry.getKey().split("::")[0]));
            history.setVid(Long.parseLong(entry.getKey().split("::")[1]));
            history.setPart(Integer.valueOf(data[0]));
            history.setWatchTime(Integer.valueOf(data[1]));
            history.setUpdateTime(LocalDateTime.parse(data[2]));

            list.add(history);
        }

        // 批量入库
        historyMapper.upsert(list);
    }

    // 获取缓存数据
    private Map<String,String> getHistoryCacheData(Long uid,Long vid){
        try {
            String[] data=(redisService.getCacheMap(HISTORY_KEY).get(uid+"::"+vid)).toString().split("::");
            return Map.of("part",data[0],"watchTime",data[1],"updateTime",data[2]);
        }catch (Exception e){
            return null;
        }
    }


    // 删除缓存数据
    private void deleteHistoryCacheData(Long uid,Long vid){
        redisService.deleteCacheMapValue(HISTORY_KEY,uid+"::"+vid);
    }

    // 更新缓存数据
    private void updateHistoryCacheData(Long uid, Long vid, Integer part, Integer watchTime,LocalDateTime updateTime){
        redisService.setCacheMapValue(HISTORY_KEY,uid+"::"+vid,part+"::"+watchTime+"::"+updateTime);
    }

    // 刷新一条历史记录缓存到数据库
    private void flushOneHistoryToDB(Long uid,Long vid){
        // 读取
        Map<String,String> cacheData= this.getHistoryCacheData(uid,vid);

        // 保证缓存数据存在
        if(cacheData!=null) {
            // 清除历史记录列表缓存和单条缓存
            redisService.deleteByKeySuffix("userHistoryCache::"+uid);
            redisService.deleteByKeySuffix("userOneHistory::"+uid);

            historyMapper.updateOneHistory(History.builder()
                    .uid(uid)
                    .vid(vid)
                    .part(Integer.valueOf(cacheData.get("part")))
                    .watchTime(Integer.valueOf(cacheData.get("watchTime")))
                    .updateTime(LocalDateTime.parse(cacheData.get("updateTime")))
                    .build()
            );
        }
    }


    @Override
    @Cacheable(value = "userHistoryCache",key = "#uid+'::'+#keyWord+'::'+#pageNum+'::'+#pageSize",sync = true)
    public List<UserHistoryInfoVo> getUserHistory(Long uid,String keyWord,int pageNum, int pageSize) {
        List<UserHistoryInfoVo> data = historyMapper.getUserHistory(SecurityUtils.getUserId(),keyWord);
        data.forEach(history ->{
            // 读取缓存参数
            Map<String,String> cacheData=this.getHistoryCacheData(history.getUid(),history.getVid());

            // 写入
            if (cacheData != null) {
                history.setWatchTime(Integer.valueOf(cacheData.get("watchTime")));
                history.setPart(Integer.valueOf(cacheData.get("part")));
                history.setUpdateTime(LocalDateTime.parse(cacheData.get("updateTime")));
            }

            // 查询视频信息
            history.setVideo(videoMapper.getVideo(history.getVid()));
        });
        return data;
    }

    // 获取指定的历史记录
    @Override
    public History getUserOneHistory(Long uid,Long vid) {
        Map<String,String> cacheData=this.getHistoryCacheData(uid,vid);
        if (cacheData ==null){
            return historyMapper.getUserOneHistory(uid,vid);
        }
        return History.builder()
                .uid(uid)
                .vid(vid)
                .watchTime(cacheData.get("watchTime") == null ? 0 : Integer.parseInt(cacheData.get("watchTime")))
                .part(cacheData.get("part") == null ? 0 : Integer.parseInt(cacheData.get("part")))
                .updateTime(cacheData.get("updateTime") == null ? null : LocalDateTime.parse(cacheData.get("updateTime")))
                .build();
    }

    @Override
    // 清除缓存
    @Caching(evict = {
            @CacheEvict(value = "userOneHistory",key = "#uid+'-'+#vid")
    })
    // 根据视频id和用户id删除历史记录
    public boolean removeHistory(Long uid,Long vid) {
        // 清除缓存
        redisService.deleteByKeySuffix("userHistoryCache::"+uid+"::");
        // 清除缓存
        this.deleteHistoryCacheData(uid,vid);
        // 调用historyMapper的removeHistory方法,删除历史记录
        return historyMapper.deleteHistory(uid,vid)>0;
    }




    // 具体的逻辑
    // 缓存中没有这个历史记录数据，直接写入到数据库中去
    // 存在就更新缓存数据，易改动数据存入redis缓存
    // 留一个flush刷新服务，用于视频结束后写入更新时间进行排序
    @Override
    @Caching(put = {
            @CachePut(value = "userOneHistory",key = "#history.uid+'-'+#history.vid")
    })
    public void setHistory(History history) {

        // 检测是否有这个记录，有的话写入缓存，没有就写入数据库
        if (this.getHistoryCacheData(history.getUid(), history.getVid())==null){
            // 写入缓存
            this.updateHistoryCacheData(history.getUid(),history.getVid(),history.getPart(),history.getWatchTime(),LocalDateTime.now());
            // 刷新入库
            this.flushOneHistoryToDB(history.getUid(),history.getVid());
        }else{
            // 写入缓存
            this.updateHistoryCacheData(history.getUid(),history.getVid(),history.getPart(),history.getWatchTime(),LocalDateTime.now());
        }
    }

    @Override
    public void flushHistory(Long vid) {
        // 刷新缓存
        this.flushOneHistoryToDB(SecurityUtils.getUserId(),vid);
    }
}
