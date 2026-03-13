package com.ruoyi.video.admin.service.impl;

import com.ruoyi.video.admin.mapper.UpdaterLogMapper;
import com.ruoyi.video.admin.service.UpdaterLogService;
import com.ruoyi.video.domain.UpdaterLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 更新器日志Service业务层处理
 *
 * @author ruoyi
 * @date 2025-08-09
 */
@Service
public class UpdaterLogServiceImpl implements UpdaterLogService
{
    @Autowired
    private UpdaterLogMapper updaterLogMapper;

    /**
     * 查询更新器日志
     *
     * @param id 更新器日志主键
     * @return 更新器日志
     */
    @Override
    public UpdaterLog selectUpdaterLogById(String id)
    {
        return updaterLogMapper.selectUpdaterLogById(id);
    }

    /**
     * 查询更新器日志列表
     *
     * @param updaterLog 更新器日志
     * @return 更新器日志
     */
    @Override
    public List<UpdaterLog> selectUpdaterLogList(UpdaterLog updaterLog)
    {
        return updaterLogMapper.selectUpdaterLogList(updaterLog);
    }

    /**
     * 新增更新器日志
     *
     * @param updaterLog 更新器日志
     * @return 结果
     */
    @Override
    public int insertUpdaterLog(UpdaterLog updaterLog)
    {
        return updaterLogMapper.insertUpdaterLog(updaterLog);
    }

    /**
     * 修改更新器日志
     *
     * @param updaterLog 更新器日志
     * @return 结果
     */
    @Override
    public int updateUpdaterLog(UpdaterLog updaterLog)
    {
        return updaterLogMapper.updateUpdaterLog(updaterLog);
    }

    /**
     * 批量删除更新器日志
     *
     * @param ids 需要删除的更新器日志主键
     * @return 结果
     */
    @Override
    public int deleteUpdaterLogByIds(String[] ids)
    {
        return updaterLogMapper.deleteUpdaterLogByIds(ids);
    }

    /**
     * 删除更新器日志信息
     *
     * @param id 更新器日志主键
     * @return 结果
     */
    @Override
    public int deleteUpdaterLogById(String id)
    {
        return updaterLogMapper.deleteUpdaterLogById(id);
    }

    @Override
    public int deleteAllUpdaterLog() {
        updaterLogMapper.deleteAllUpdaterLog();
        return 1;
    }
}
