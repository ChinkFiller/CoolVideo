package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.SysOssConfig;
import com.ruoyi.system.mapper.SysOssConfigMapper;
import com.ruoyi.system.service.ISysOssClientService;
import com.ruoyi.system.service.ISysOssConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

import java.util.List;

/**
 * 对象存储配置Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-16
 */
@Service
public class SysOssConfigServiceImpl implements ISysOssConfigService
{
    @Autowired
    private SysOssConfigMapper sysOssConfigMapper;

    @Autowired
    ISysOssClientService ossClientService;



    private static final Logger logger = LoggerFactory.getLogger("oss-config");


    /**
     * 查询对象存储配置
     *
     * @param ossConfigId 对象存储配置主键
     * @return 对象存储配置
     */
    @Override
    public SysOssConfig selectSysOssConfigByOssConfigId(Long ossConfigId)
    {
        return sysOssConfigMapper.selectSysOssConfigByOssConfigId(ossConfigId);
    }

    /**
     * 查询对象存储配置列表
     *
     * @param sysOssConfig 对象存储配置
     * @return 对象存储配置
     */
    @Override
    public List<SysOssConfig> selectSysOssConfigList(SysOssConfig sysOssConfig)
    {
        return sysOssConfigMapper.selectSysOssConfigList(sysOssConfig);
    }

    /**
     * 新增对象存储配置
     *
     * @param sysOssConfig 对象存储配置
     * @return 结果
     */
    @Override
    public int insertSysOssConfig(SysOssConfig sysOssConfig)
    {
        sysOssConfig.setCreateTime(DateUtils.getNowDate());
        return sysOssConfigMapper.insertSysOssConfig(sysOssConfig);
    }

    /**
     * 修改对象存储配置
     *
     * @param sysOssConfig 对象存储配置
     * @return 结果
     */
    @Override
    @Transactional
    public int updateSysOssConfig(SysOssConfig sysOssConfig)
    {
        sysOssConfig.setUpdateTime(DateUtils.getNowDate());
        sysOssConfigMapper.updateSysOssConfig(sysOssConfig);
        if (sysOssConfig.getStatus().equals("1")){
            ossClientService.reloadClientConfig();
        }
        return 1;
    }

    /**
     * 批量删除对象存储配置
     *
     * @param ossConfigIds 需要删除的对象存储配置主键
     * @return 结果
     */
    @Override
    public int deleteSysOssConfigByOssConfigIds(Long[] ossConfigIds)
    {
        return sysOssConfigMapper.deleteSysOssConfigByOssConfigIds(ossConfigIds);
    }

    /**
     * 删除对象存储配置信息
     *
     * @param ossConfigId 对象存储配置主键
     * @return 结果
     */
    @Override
    public int deleteSysOssConfigByOssConfigId(Long ossConfigId)
    {
        return sysOssConfigMapper.deleteSysOssConfigByOssConfigId(ossConfigId);
    }

    @Override
    public SysOssConfig getOssConfig() {
        return sysOssConfigMapper.selectDefaultConfig();
    }

    @Override
    public void testConfig(Long ossConfigId) {
        SysOssConfig config=sysOssConfigMapper.selectSysOssConfigByOssConfigId(ossConfigId);
        if (config==null) {
            throw new RuntimeException("配置不存在");
        }
        ListObjectsV2Request.Builder builder = ListObjectsV2Request.builder()
                .bucket(config.getBucketName())
                .maxKeys(10); // 每次最多拉取1000个
        try {
            ossClientService.createTemClient(config).listObjectsV2(builder.build());
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("配置测试失败");
        }
    }

    @Override
    public int updateStatus(SysOssConfig sysOssConfig) {
        try {
            this.testConfig(sysOssConfig.getOssConfigId());
        }catch (Exception e){
            throw new RuntimeException("该配置测试失败，无法设置为默认存储");
        }
        // 除了这个ID外，全部设置为非默认
        sysOssConfigMapper.setOnlyDefaultConfig(sysOssConfig.getOssConfigId());
        // 默认的存储发生了改变，重新初始化客户端
        ossClientService.reloadClientConfig();
        return 1;

    }


}
