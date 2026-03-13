package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysAppConfig;

import java.util.List;

/**
 * App端配置信息管理Mapper接口
 *
 * @author ruoyi
 * @date 2025-10-15
 */
public interface SysAppConfigMapper
{
    /**
     * 查询App端配置信息管理
     *
     * @param id App端配置信息管理主键
     * @return App端配置信息管理
     */
    public SysAppConfig selectSysAppConfigById(Long id);

    /**
     * 查询App端配置信息管理列表
     *
     * @param sysAppConfig App端配置信息管理
     * @return App端配置信息管理集合
     */
    public List<SysAppConfig> selectSysAppConfigList(SysAppConfig sysAppConfig);

    /**
     * 新增App端配置信息管理
     *
     * @param sysAppConfig App端配置信息管理
     * @return 结果
     */
    public int insertSysAppConfig(SysAppConfig sysAppConfig);

    /**
     * 修改App端配置信息管理
     *
     * @param sysAppConfig App端配置信息管理
     * @return 结果
     */
    public int updateSysAppConfig(SysAppConfig sysAppConfig);

    /**
     * 删除App端配置信息管理
     *
     * @param id App端配置信息管理主键
     * @return 结果
     */
    public int deleteSysAppConfigById(Integer id);

    /**
     * 批量删除App端配置信息管理
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysAppConfigByIds(Integer[] ids);

    SysAppConfig getAppUpdateInfo(SysAppConfig appConfig);
}
