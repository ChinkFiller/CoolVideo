package com.ruoyi.video.admin.mapper;

import com.ruoyi.video.admin.domain.VideoStorage;

import java.util.List;


public interface VideoStorageMapper {
    /**
     * 查询视频资源信息
     *
     * @param id 视频资源信息主键
     * @return 视频资源信息
     */
    public VideoStorage selectVideoStorageById(Long id);

    /**
     * 查询视频资源信息列表
     *
     * @param VideoStorage 视频资源信息
     * @return 视频资源信息集合
     */
    public List<VideoStorage> selectVideoStorageList(VideoStorage VideoStorage);

    /**
     * 新增视频资源信息
     *
     * @param VideoStorage 视频资源信息
     * @return 结果
     */
    public int insertVideoStorage(VideoStorage VideoStorage);

    /**
     * 修改视频资源信息
     *
     * @param VideoStorage 视频资源信息
     * @return 结果
     */
    public int updateVideoStorage(VideoStorage VideoStorage);

    /**
     * 删除视频资源信息
     *
     * @param id 视频资源信息主键
     * @return 结果
     */
    public int deleteVideoStorageById(Long id);

    /**
     * 批量删除视频资源信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteVideoStorageByIds(Long[] ids);
}
