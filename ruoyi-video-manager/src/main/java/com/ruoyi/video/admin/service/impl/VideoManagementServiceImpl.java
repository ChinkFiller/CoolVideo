package com.ruoyi.video.admin.service.impl;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.service.ISysJobService;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.video.admin.domain.form.UpdateConfig;
import com.ruoyi.video.admin.domain.vo.VideoTotalDataVo;
import com.ruoyi.video.admin.mapper.VideoManagementMapper;
import com.ruoyi.video.admin.mapper.WeekTableManagementMapper;
import com.ruoyi.video.admin.service.VideoManagementService;
import com.ruoyi.video.admin.service.WeekManagementService;
import com.ruoyi.video.domain.Video;
import com.ruoyi.video.domain.WeekTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 视频数据保存的数据Service业务层处理
 *
 * @author ruoyi
 * @date 2025-07-19
 */
@Service
public class VideoManagementServiceImpl implements VideoManagementService
{
    @Autowired
    private VideoManagementMapper videoControlMapper;

    @Autowired
    ISysJobService jobService;

    @Autowired
    RedisCache redisService;

    @Autowired
    ISysConfigService configService;

    @Autowired
    WeekTableManagementMapper weekTableManagementMapper;



    /**
     * 查询视频数据保存的数据
     *
     * @param id 视频数据保存的数据主键
     * @return 视频数据保存的数据
     */
    @Override
    public Video selectVideoById(Long id)
    {
        return videoControlMapper.selectVideoById(id);
    }

    /**
     * 查询视频数据保存的数据列表
     *
     * @param filmData 视频数据保存的数据
     * @return 视频数据保存的数据
     */
    @Override
    public List<Video> selectVideoList(Video filmData)
    {
        return videoControlMapper.selectVideoList(filmData);
    }

    /**
     * 新增视频数据保存的数据
     *
     * @param filmData 视频数据保存的数据
     * @return 结果
     */
    @Override
    public int insertVideo(Video filmData)
    {
        return videoControlMapper.insertVideo(filmData);
    }

    /**
     * 修改视频数据保存的数据
     *
     * @param filmData 视频数据保存的数据
     * @return 结果
     */
    @Override
    @Transactional
    public int updateVideo(Video filmData)
    {
        if (filmData.getState().equals("1")){
            // 正在更新，进行修改或更新
            weekTableManagementMapper.updateWeekByVid(WeekTable.builder()
                    .week(filmData.getWeek())
                    .vid(filmData.getId())
                    .build()
            );
        }else{
            // 如果修改为已停更，那么删除这个周期表数据
            weekTableManagementMapper.deleteWeekByVid(filmData.getId());
        }
        videoControlMapper.updateVideoTagByVideoId(filmData.getTags(), filmData.getId());
        return videoControlMapper.updateVideo(filmData);
    }

    /**
     * 批量删除视频数据保存的数据
     *
     * @param ids 需要删除的视频数据保存的数据主键
     * @return 结果
     */
    @Override
    public int deleteVideoByIds(Long[] ids)
    {
        return videoControlMapper.deleteVideoByIds(ids);
    }

    /**
     * 删除视频数据保存的数据信息
     *
     * @param id 视频数据保存的数据主键
     * @return 结果
     */
    @Override
    public int deleteVideoById(Long id)
    {
        return videoControlMapper.deleteVideoById(id);
    }

    @Override
    public int setAutoUpdateConfig(UpdateConfig config){
        SysJob job=jobService.selectJobById(100L);
        SysConfig autoUpdateConfig=configService.selectFullConfigByKey("video.autoupdate.enable");
        //设置是否自动更新
        job.setStatus(config.getAutoUpdate()?"0":"1");
        autoUpdateConfig.setConfigValue(config.getAutoUpdate()?"true":"false");

        //更新配置
        configService.updateConfig(autoUpdateConfig);

        //设置更新间隔
        if (config.getUpdateInterval()<=23 && config.getUpdateInterval()>=1){
            job.setCronExpression("0 0 0/"+config.getUpdateInterval()+" * * ?");
        }

        try {
            return jobService.updateJob(job);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("设置自动更新失败");
        }
    }

    @Override
    public UpdateConfig getAutoUpdateConfig() {
        SysJob job=jobService.selectJobById(100L);
        UpdateConfig config=new UpdateConfig();
        config.setAutoUpdate(job.getStatus().equals("0"));
        config.setUpdateInterval(Integer.parseInt(job.getCronExpression().split("/")[1].split(" ")[0]));
        return config;
    }

    @Override
    public int runUpdate() {
        SysJob job=jobService.selectJobById(100L);
        try {
            return jobService.run(job)?1:0;
        }catch (Exception e){
            throw new RuntimeException("启动更新器失败，请检查更新器配置");
        }
    }

    private List<Map<String, Object>> getTop10(Map<String, Integer> data) {
        return data.entrySet().stream()
                // 按 value 倒序排序
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                // 取前 10 个
                .limit(10)
                // 转成 List<Map<String, Object>>
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", Integer.parseInt(entry.getKey()));
                    map.put("number", entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }
    @Override
    public VideoTotalDataVo getTotalInfo() {
        VideoTotalDataVo data=new VideoTotalDataVo();
        data.setTopLike(videoControlMapper.getTopLike());
        data.setTopPlayCount(this.getTop10(redisService.getCacheMap("playCount")));
        return data;
    }

    @Override
    public Boolean isVideoExist(Long vid, Integer part) {
        //获取视频信息
        Video data=videoControlMapper.selectVideoById(vid);
        if (data==null){
            return false;
        }else return data.getPart() >= part;
    }

    @Override
    public Boolean lockInfo(Long id) {
        return videoControlMapper.lockInfo(id);
    }

    @Override
    public Boolean unLockInfo(Long id) {
        return videoControlMapper.unLockInfo(id);
    }

}
