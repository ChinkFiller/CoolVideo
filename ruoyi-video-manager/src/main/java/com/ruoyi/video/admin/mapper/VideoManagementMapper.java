package com.ruoyi.video.admin.mapper;

import com.ruoyi.video.domain.Video;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 视频数据保存的数据Mapper接口
 *
 * @author ruoyi
 * @date 2025-07-19
 */
public interface VideoManagementMapper
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
     * 删除视频数据保存的数据
     *
     * @param id 视频数据保存的数据主键
     * @return 结果
     */
    public int deleteVideoById(Long id);

    /**
     * 批量删除视频数据保存的数据
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteVideoByIds(Long[] ids);

    @MapKey("id")
    List<Map<String, Object>> getTopLike();

    List<Long> selectVideoIdByTagCode(String tagCode);

    int updateVideoTagByTagCode(@Param("ids") List<Long> ids,@Param("tag") String tag);
    int updateVideoTagByVideoId(@Param("tags") List<String> tags,@Param("vid") Long vid);
    int deleteVideoTag(Long[] tagIds);

    Boolean lockInfo(Long id);

    Boolean unLockInfo(Long id);
}
