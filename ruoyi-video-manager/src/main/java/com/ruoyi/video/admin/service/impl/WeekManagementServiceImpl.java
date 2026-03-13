package com.ruoyi.video.admin.service.impl;

import com.ruoyi.video.admin.mapper.WeekTableManagementMapper;
import com.ruoyi.video.admin.service.WeekManagementService;
import com.ruoyi.video.domain.WeekTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 视频周期Service业务层处理
 *
 * @author ruoyi
 * @date 2025-08-07
 */
@Service
public class WeekManagementServiceImpl implements WeekManagementService
{
    @Autowired
    private WeekTableManagementMapper weekMapper;

    /**
     * 查询视频周期
     *
     * @param id 视频周期主键
     * @return 视频周期
     */
    @Override
    public WeekTable selectWeekById(Long id)
    {
        return weekMapper.selectWeekById(id);
    }

    /**
     * 查询视频周期列表
     *
     * @param week 视频周期
     * @return 视频周期
     */
    @Override
    public List<WeekTable> selectWeekList(WeekTable week)
    {
        return weekMapper.selectWeekList(week);
    }

    /**
     * 新增视频周期
     *
     * @param week 视频周期
     * @return 结果
     */
    @Override
    public int insertWeek(WeekTable week)
    {
        return weekMapper.insertWeek(week);
    }

    /**
     * 修改视频周期
     *
     * @param week 视频周期
     * @return 结果
     */
    @Override
    public int updateWeek(WeekTable week)
    {
        return weekMapper.updateWeek(week);
    }

    /**
     * 批量删除视频周期
     *
     * @param ids 需要删除的视频周期主键
     * @return 结果
     */
    @Override
    public int deleteWeekByIds(Long[] ids)
    {
        return weekMapper.deleteWeekByIds(ids);
    }

    /**
     * 删除视频周期信息
     *
     * @param id 视频周期主键
     * @return 结果
     */
    @Override
    public int deleteWeekById(Long id)
    {
        return weekMapper.deleteWeekById(id);
    }
}
