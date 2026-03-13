package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对象存储配置对象 sys_oss_config
 *
 * @author ruoyi
 * @date 2025-10-16
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysOssConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long ossConfigId;

    /** 配置key */
    @Excel(name = "配置key")
    private String configKey;

    /** accessKey */
    @Excel(name = "accessKey")
    private String accessKey;

    /** 秘钥 */
    @Excel(name = "秘钥")
    private String secretKey;

    /** 桶名称 */
    @Excel(name = "桶名称")
    private String bucketName;

    /** 前缀 */
    @Excel(name = "前缀")
    private String prefix;

    /** 访问站点 */
    @Excel(name = "访问站点")
    private String endpoint;

    /** 自定义域名 */
    @Excel(name = "自定义域名")
    private String domain;

    /** 是否https（Y=是,N=否） */
    @Excel(name = "是否https", readConverterExp = "Y==是,N=否")
    private String isHttps;

    /** 域 */
    @Excel(name = "域")
    private String region;

    /** 桶权限类型(0=private 1=public 2=custom) */
    @Excel(name = "桶权限类型(0=private 1=public 2=custom)")
    private String accessPolicy;

    /** 是否默认（0=是,1=否） */
    @Excel(name = "是否默认", readConverterExp = "0==是,1=否")
    private String status;

    /** 扩展字段 */
    @Excel(name = "扩展字段")
    private String ext1;
}
