package com.ruoyi.video.admin.mapper;

import com.ruoyi.video.domain.UpdaterLog;

import java.util.List;

/**
 * 更新器日志Mapper接口
 *
 * @author ruoyi
 * @date 2025-08-09
 */
public interface UpdaterLogMapper
{
    /**
     * 查询更新器日志
     *
     * @param id 更新器日志主键
     * @return 更新器日志
     */
    public UpdaterLog selectUpdaterLogById(String id);

    /**
     * 查询更新器日志列表
     *
     * @param updaterLog 更新器日志
     * @return 更新器日志集合
     */
    public List<UpdaterLog> selectUpdaterLogList(UpdaterLog updaterLog);

    /**
     * 新增更新器日志
     *
     * @param updaterLog 更新器日志
     * @return 结果
     */
    public int insertUpdaterLog(UpdaterLog updaterLog);

    /**
     * 修改更新器日志
     *
     * @param updaterLog 更新器日志
     * @return 结果
     */
    public int updateUpdaterLog(UpdaterLog updaterLog);

    /**
     * 删除更新器日志
     *
     * @param id 更新器日志主键
     * @return 结果
     */
    public int deleteUpdaterLogById(String id);

    /**
     * 批量删除更新器日志
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUpdaterLogByIds(String[] ids);

    int deleteAllUpdaterLog();
}
