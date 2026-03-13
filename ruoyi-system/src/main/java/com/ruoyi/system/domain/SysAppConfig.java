package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * App端配置信息管理对象 sys_app_config
 *
 * @author ruoyi
 * @date 2025-10-15
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysAppConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 配置的id */
    private Long id;

    /** App的名称 */
    @Excel(name = "App的名称")
    private String appName;

    /** App的版本代码 */
    @Excel(name = "App的版本代码")
    private Long appVersion;

    /** App的ID */
    @Excel(name = "App的ID")
    private String appId;

    /** App运行的平台 */
    @Excel(name = "App运行的平台")
    private String plt;

    /** App最新版下载地址 */
    @Excel(name = "App最新版下载地址")
    private String updateUrl;

    /** 最低支持的版本 */
    @Excel(name = "最低支持的版本")
    private Long finalVersion;

    /** App的状态0为停用，1为启用 */
    @Excel(name = "App的状态0为停用，1为启用")
    private String state;

    /** 是否强制更新，1为强制更新，0为非强制更新 */
    private String forceUpdate;

    /** 最新版的更新描述 **/
    @Excel(name = "最新版的更新描述")
    private String description;
}
