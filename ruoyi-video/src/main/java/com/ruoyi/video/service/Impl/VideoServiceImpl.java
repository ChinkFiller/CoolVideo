package com.ruoyi.video.service.Impl;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.ResultCodes;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.video.domain.Video;
import com.ruoyi.video.mapper.VideoMapper;
import com.ruoyi.video.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    VideoMapper videoMapper;

    @Autowired
    RedisCache redisService;


    @Override
    // 使用缓存，缓存名为videoCache，key为id
    @Cacheable(value = "videoCache",key = "#id",sync = true)
    public Video getOneVideo(Long id) {
        // 根据id获取视频数据
        Video v= videoMapper.getVideo(id);
        // 如果视频数据为空，抛出视频不存在的异常
        if (v==null){
            throw new RuntimeException(ResultCodes.NOT_FOUND.getMsg());
        }
        // 返回视频数据
        return v;
    }

    @Override
    @Cacheable(value = "videoSearchCache",key = "#keyWord+'::'+#pageNum+'::'+#pageSize",sync = true)
    public List<Video> searchVideo(String keyWord, Integer pageNum, Integer pageSize) {
        return videoMapper.searchVideo(keyWord);
    }



    @Override
    //清除用户的点赞缓存
    @Caching(evict = {
            @CacheEvict(value = "videoCache",key = "#vid"),
            @CacheEvict(value = "userLikeCache",key = "T(com.ruoyi.common.utils.SecurityUtils).userId+'-'+#vid")
    })
    public boolean agreeOrDisagreeVideo(Long vid) {
        //判断用户是否已经点赞过该视频
        if (videoMapper.isUserLikeThisVideo(SecurityUtils.getUserId(),vid)==1){
            try {
                // 如果已经点赞过，则取消点赞
                return videoMapper.disAgreeVideo(SecurityUtils.getUserId(),vid)>0;
            }catch (Exception e){
                // 取消点赞失败，抛出异常
                throw new RuntimeException(ResultCodes.DISAGREE_FAIL.getMsg());
            }
        }else{
            try {
                // 如果未点赞过，则点赞
                return videoMapper.agreeVideo(SecurityUtils.getUserId(),vid)>0;
            }catch (Exception e){
                e.printStackTrace();
                // 点赞失败，抛出异常
                throw new RuntimeException(ResultCodes.AGREE_FAIL.getMsg());
            }
        }
    }

    @Override
    @Cacheable(value = "videoFilterCache", key = "#order+'-'+#letter+'-'+#year+'-'+#pageNum+'-'+#pageSize",sync = true)
    public List<Video> searchVideoWithFilter(String order, String letter, String category, Integer year, Integer type, Integer pageNum, Integer pageSize) {
        return videoMapper.getVideoByFilter(order,letter,year);
    }


    @Override
    @Cacheable(value = "userSubscribeListCache",key = "T(com.ruoyi.common.utils.SecurityUtils).getUserId()+'::'+#keyWord+'::'+#pageNum+'::'+#pageSize",sync = true)
    public List<Video> getUserSubscribeVideo(String keyWord, Integer pageNum, Integer pageSize) {
        return videoMapper.getUserSubscribeVideo(SecurityUtils.getUserId(),keyWord);
    }

    // 判断用户是否点赞了该视频，如果点赞了返回1，否则返回0
    @Override
    @Cacheable(value = "userLikeCache",key = "#uid+'-'+#vid",sync = true)
    public int isUserLikeThisVideo(Long uid, Long vid) {
        return videoMapper.isUserLikeThisVideo(uid,vid);
    }


    @Override
    @Cacheable(value = "userSubscribeCache", key = "#uid+'-'+#vid",sync = true)
    public int isUserSubscribeThisVideo(Long uid, Long vid) {
        return videoMapper.isUserSubscribeThisVideo(uid,vid);
    }


    @Override
    @Caching(evict = {
            @CacheEvict(value = "videoCache",key = "#vid"),
            @CacheEvict(value = "userSubscribeCache",key="T(com.ruoyi.common.utils.SecurityUtils).userId+'-'+#vid")
    })
    public boolean subscribeVideoOrCancelSubscribe(Long vid) {
        Long uid= SecurityUtils.getUserId();
        redisService.deleteByKeySuffix("userSubscribeListCache::"+uid+"::");
        if (videoMapper.isUserSubscribeThisVideo(uid,vid)==1){
            try{
                return videoMapper.cancelSubscribeVideo(uid,vid)>0;
            }catch (Exception e){
                throw new RuntimeException(ResultCodes.SUBSCRIBE_CANCEL_ERROR.getMsg());
            }
        }else{
            try {
                videoMapper.subscribeVideo(uid,vid);
                return true;
            }catch (Exception e){
                throw new RuntimeException(ResultCodes.SUBSCRIBE_FAIL.getMsg());
            }
        }
    }

    @Override
    public boolean isVideoExist(Long vid, Integer part) {
        //获取视频信息
        Video data=videoMapper.getVideo(vid);
        if (data==null){
            return false;
        }else return data.getPart() >= part;
    }



}
