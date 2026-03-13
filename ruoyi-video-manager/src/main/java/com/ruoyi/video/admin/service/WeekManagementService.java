package com.ruoyi.video.admin.service;

import com.ruoyi.video.domain.WeekTable;

import java.util.List;

/**
 * 视频周期Service接口
 *
 * @author ruoyi
 * @date 2025-08-07
 */
public interface WeekManagementService
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
     * 批量删除视频周期
     *
     * @param ids 需要删除的视频周期主键集合
     * @return 结果
     */
    public int deleteWeekByIds(Long[] ids);

    /**
     * 删除视频周期信息
     *
     * @param id 视频周期主键
     * @return 结果
     */
    public int deleteWeekById(Long id);
}
