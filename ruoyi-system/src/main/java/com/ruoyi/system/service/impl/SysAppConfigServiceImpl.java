package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.SysAppConfig;
import com.ruoyi.system.mapper.SysAppConfigMapper;
import com.ruoyi.system.service.ISysAppConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * App端配置信息管理Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-15
 */
@Service
public class SysAppConfigServiceImpl implements ISysAppConfigService
{
    @Autowired
    private SysAppConfigMapper sysAppConfigMapper;

    /**
     * 查询App端配置信息管理
     *
     * @param id App端配置信息管理主键
     * @return App端配置信息管理
     */
    @Override
    public SysAppConfig selectSysAppConfigById(Long id)
    {
        return sysAppConfigMapper.selectSysAppConfigById(id);
    }

    /**
     * 查询App端配置信息管理列表
     *
     * @param sysAppConfig App端配置信息管理
     * @return App端配置信息管理
     */
    @Override
    public List<SysAppConfig> selectSysAppConfigList(SysAppConfig sysAppConfig)
    {
        return sysAppConfigMapper.selectSysAppConfigList(sysAppConfig);
    }

    /**
     * 新增App端配置信息管理
     *
     * @param sysAppConfig App端配置信息管理
     * @return 结果
     */
    @Override
    public int insertSysAppConfig(SysAppConfig sysAppConfig)
    {
        return sysAppConfigMapper.insertSysAppConfig(sysAppConfig);
    }

    /**
     * 修改App端配置信息管理
     *
     * @param sysAppConfig App端配置信息管理
     * @return 结果
     */
    @Override
    @CacheEvict(value ="sys_app_config",key = "#sysAppConfig.appId+'-'+#sysAppConfig.plt")
    public int updateSysAppConfig(SysAppConfig sysAppConfig)
    {
        return sysAppConfigMapper.updateSysAppConfig(sysAppConfig);
    }

    /**
     * 批量删除App端配置信息管理
     *
     * @param ids 需要删除的App端配置信息管理主键
     * @return 结果
     */
    @Override
    @CacheEvict(value ="sys_app_config",allEntries = true)
    public int deleteSysAppConfigByIds(Integer[] ids)
    {
        return sysAppConfigMapper.deleteSysAppConfigByIds(ids);
    }

    /**
     * 删除App端配置信息管理信息
     *
     * @param id App端配置信息管理主键
     * @return 结果
     */
    @Override
    @CacheEvict(value ="sys_app_config",allEntries = true)
    public int deleteSysAppConfigById(Integer id)
    {
        return sysAppConfigMapper.deleteSysAppConfigById(id);
    }

    @Override
    @Cacheable(value = "sys_app_config",key = "#sysAppConfig.appId+'-'+#sysAppConfig.plt")
    public SysAppConfig getAppUpdateInfo(SysAppConfig sysAppConfig) {
        return sysAppConfigMapper.getAppUpdateInfo(sysAppConfig);
    }
}
