package com.ruoyi.video.admin.service;

import com.ruoyi.video.domain.UpdaterLog;

import java.util.List;

/**
 * 更新器日志Service接口
 *
 * @author ruoyi
 * @date 2025-08-09
 */
public interface UpdaterLogService
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
     * 批量删除更新器日志
     *
     * @param ids 需要删除的更新器日志主键集合
     * @return 结果
     */
    public int deleteUpdaterLogByIds(String[] ids);

    /**
     * 删除更新器日志信息
     *
     * @param id 更新器日志主键
     * @return 结果
     */
    public int deleteUpdaterLogById(String id);

    int deleteAllUpdaterLog();
}
