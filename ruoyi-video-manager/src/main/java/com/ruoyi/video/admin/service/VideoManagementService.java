package com.ruoyi.video.admin.service;


import com.ruoyi.video.admin.domain.form.UpdateConfig;
import com.ruoyi.video.admin.domain.vo.VideoTotalDataVo;
import com.ruoyi.video.domain.Video;

import java.util.List;

/**
 * 视频数据保存的数据Service接口
 *
 * @author ruoyi
 * @date 2025-07-19
 */
public interface VideoManagementService
{
    /**
     * 查询视频数据保存的数据
     *
     * @param id 视频数据保存的数据主键
     * @return 视频数据保存的数据
     */
    public Video selectVideoById(Long id);

    /**
     * 查询视频数据保存的数据列表
     *
     * @param filmData 视频数据保存的数据
     * @return 视频数据保存的数据集合
     */
    public List<Video> selectVideoList(Video filmData);

    /**
     * 新增视频数据保存的数据
     *
     * @param filmData 视频数据保存的数据
     * @return 结果
     */
    public int insertVideo(Video filmData);

    /**
     * 修改视频数据保存的数据
     *
     * @param filmData 视频数据保存的数据
     * @return 结果
     */
    public int updateVideo(Video filmData);

    /**
     * 批量删除视频数据保存的数据
     *
     * @param ids 需要删除的视频数据保存的数据主键集合
     * @return 结果
     */
    public int deleteVideoByIds(Long[] ids);

    /**
     * 删除视频数据保存的数据信息
     *
     * @param id 视频数据保存的数据主键
     * @return 结果
     */
    public int deleteVideoById(Long id);


    int setAutoUpdateConfig(UpdateConfig config);

    UpdateConfig getAutoUpdateConfig();

    int runUpdate();

    VideoTotalDataVo getTotalInfo();

    Boolean isVideoExist(Long vid,Integer part);

    Boolean lockInfo(Long id);

    Boolean unLockInfo(Long id);
}
