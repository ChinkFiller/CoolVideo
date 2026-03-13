package com.ruoyi.video.admin.mapper;

import com.ruoyi.video.domain.WeekTable;

import java.util.List;

/**
 * 视频周期Mapper接口
 *
 * @author ruoyi
 * @date 2025-08-07
 */
public interface WeekTableManagementMapper
{
    /**
     * 查询视频周期
     *
     * @param id 视频周期主键
     * @return 视频周期
     */
    public WeekTable selectWeekById(Long id);

    /**
     * 查询视频周期列表
     *
     * @param week 视频周期
     * @return 视频周期集合
     */
    public List<WeekTable> selectWeekList(WeekTable week);

    /**
     * 新增视频周期
     *
     * @param week 视频周期
     * @return 结果
     */
    public int insertWeek(WeekTable week);

    /**
     * 修改视频周期
     *
     * @param week 视频周期
     * @return 结果
     */
    public int updateWeek(WeekTable week);

    /**
     * 删除视频周期
     *
     * @param id 视频周期主键
     * @return 结果
     */
    public int deleteWeekById(Long id);

    /**
     * 批量删除视频周期
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWeekByIds(Long[] ids);

    int updateWeekByVid(WeekTable week);

    int deleteWeekByVid(Long id);
    int isVideoOnGoing(Long vid);
}
