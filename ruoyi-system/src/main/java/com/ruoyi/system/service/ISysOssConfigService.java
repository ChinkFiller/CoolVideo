package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysOssConfig;

import java.util.List;

/**
 * 对象存储配置Service接口
 *
 * @author ruoyi
 * @date 2025-10-16
 */
public interface ISysOssConfigService
{
    /**
     * 查询对象存储配置
     *
     * @param ossConfigId 对象存储配置主键
     * @return 对象存储配置
     */
    public SysOssConfig selectSysOssConfigByOssConfigId(Long ossConfigId);

    /**
     * 查询对象存储配置列表
     *
     * @param sysOssConfig 对象存储配置
     * @return 对象存储配置集合
     */
    public List<SysOssConfig> selectSysOssConfigList(SysOssConfig sysOssConfig);

    /**
     * 新增对象存储配置
     *
     * @param sysOssConfig 对象存储配置
     * @return 结果
     */
    public int insertSysOssConfig(SysOssConfig sysOssConfig);

    /**
     * 修改对象存储配置
     *
     * @param sysOssConfig 对象存储配置
     * @return 结果
     */
    public int updateSysOssConfig(SysOssConfig sysOssConfig);

    /**
     * 批量删除对象存储配置
     *
     * @param ossConfigIds 需要删除的对象存储配置主键集合
     * @return 结果
     */
    public int deleteSysOssConfigByOssConfigIds(Long[] ossConfigIds);

    /**
     * 删除对象存储配置信息
     *
     * @param ossConfigId 对象存储配置主键
     * @return 结果
     */
    public int deleteSysOssConfigByOssConfigId(Long ossConfigId);

    /**
     *
     * 获取当前的OSS存储配置
     *
     */
    public SysOssConfig getOssConfig();

    void testConfig(Long ossConfigId);

    int updateStatus(SysOssConfig sysOssConfig);
}
