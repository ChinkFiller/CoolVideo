package com.ruoyi.video.controller;


import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.ResultCodes;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.video.domain.Video;
import com.ruoyi.video.domain.vo.VideoInfoVo;
import com.ruoyi.video.service.PlayerInfoService;
import com.ruoyi.video.service.VideoRecommendService;
import com.ruoyi.video.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/video")
@Tag(name = "视频服务", description = "视频信息服务")
public class VideoController extends BaseController {


    // 视频服务
    @Autowired
    VideoService videoService;

    // 播放器服务
    @Autowired
    PlayerInfoService playerInfoService;

    // 视频推荐服务
    @Autowired
    VideoRecommendService recommendService;

    @Autowired
    RedisCache redisService;



    @Operation(summary = "模糊搜索一个视频")
    @GetMapping("/search")
    public TableDataInfo searchVideo(
        @RequestParam("key") String key,
        @RequestParam(value = "pageNum",required = false,defaultValue = "1") Integer pageNum,
        @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize
    ){
        startPage();
        List<Video> list=videoService.searchVideo(key,pageNum, pageSize);
        return getDataTable(list);
    }


    @Operation(summary = "模糊搜索一个视频")
    @GetMapping("/app/search")
    public AjaxResult searchVideo(
            @RequestParam("key") String key
    ){
        return success(videoService.searchVideo(key,1,10));
    }

    @Operation(summary = "获取推荐视频")
    @GetMapping("/recommend")
    public AjaxResult recommend(
            @RequestParam("id") Long id
    ){
        return AjaxResult.success(recommendService.getRecommendList(id));
    }

    @Anonymous
    @Operation(summary = "获取一个视频的详细信息")
    @GetMapping("/detail/{id}")
    public AjaxResult video(
            @PathVariable("id") Long id
    ) {
        VideoInfoVo v=new VideoInfoVo().getVideoInfoVo(videoService.getOneVideo(id));
        //设置播放量
        v.setPlayCount(redisService.getCacheMapValue("playCount",id.toString()));
        //判断用户的订阅和点赞
        try {
            v.setIsLike(videoService.isUserLikeThisVideo(SecurityUtils.getUserId(),id));
            v.setIsSubscribe(videoService.isUserSubscribeThisVideo(SecurityUtils.getUserId(),id));
        }catch (Exception ignored){
        }
        return AjaxResult.success(v);
    }


    @Operation(summary = "获取一个视频播放信息")
    @GetMapping("/watch/{id}")
    public AjaxResult watch(
            @PathVariable("id") Long id
    ){
        return AjaxResult.success(playerInfoService.getPlayerInfo(id));
    }


    @Operation(summary = "条件筛选")
    @GetMapping("/filter")
    public TableDataInfo filter(
            @RequestParam(value = "order",required = false) String order,
            @RequestParam(value = "letter",required = false) String letter,
            @RequestParam(value = "category",required = false) String category,
            @RequestParam(value = "year",required = false) Integer year,
            @RequestParam(value = "type") Integer type,
            @RequestParam(value = "pageNum",required = false,defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize
    ){
        startPage();
        List<Video> list=videoService.searchVideoWithFilter(order,letter,category,year,type,pageNum,pageSize);
        return getDataTable(list);
    }


    @Operation(summary = "点赞视频或取消点赞，系统自动判断")
    @GetMapping("/like/{id}")
    public AjaxResult agree(
            @PathVariable("id") Long id
    ){
        if (videoService.agreeOrDisagreeVideo(id)){
            return AjaxResult.success();
        }else{
            return AjaxResult.error(ResultCodes.AGREE_FAIL);
        }
    }


    @Operation(summary = "订阅或取消订阅视频，自动判断")
    @GetMapping("/subscribe/{id}")
    public AjaxResult subscribe(
            @PathVariable Long id
    ){
        if (videoService.subscribeVideoOrCancelSubscribe(id)){
            return AjaxResult.success();
        }else{
            return AjaxResult.error(ResultCodes.SUBSCRIBE_FAIL);
        }
    }

    @GetMapping("/user/subscribe")
    @Operation(summary = "获取用户收藏的视频")
    public TableDataInfo subscribe(
            @RequestParam(value = "keyWord", defaultValue = "") String keyWord,
            @RequestParam(value = "pageNum",required = false,defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize
    ){
        startPage();
        List<Video> list=videoService.getUserSubscribeVideo(keyWord,pageNum,pageSize);
        return getDataTable(list);
    }



}
