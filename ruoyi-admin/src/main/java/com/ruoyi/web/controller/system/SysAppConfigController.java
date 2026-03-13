package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysAppConfig;
import com.ruoyi.system.service.ISysAppConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * App端配置信息管理Controller
 *
 * @author ruoyi
 * @date 2025-10-15
 */
@RestController
@RequestMapping("/system/appconfig")
public class SysAppConfigController extends BaseController
{
    @Autowired
    private ISysAppConfigService sysAppConfigService;

    /**
     * 查询App端配置信息管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:appconfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysAppConfig sysAppConfig)
    {
        startPage();
        List<SysAppConfig> list = sysAppConfigService.selectSysAppConfigList(sysAppConfig);
        return getDataTable(list);
    }

    /**
     * 导出App端配置信息管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:appconfig:export')")
    @Log(title = "App端配置信息管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysAppConfig sysAppConfig)
    {
        List<SysAppConfig> list = sysAppConfigService.selectSysAppConfigList(sysAppConfig);
        ExcelUtil<SysAppConfig> util = new ExcelUtil<SysAppConfig>(SysAppConfig.class);
        util.exportExcel(response, list, "App端配置信息管理数据");
    }

    /**
     * 获取App端配置信息管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:appconfig:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sysAppConfigService.selectSysAppConfigById(id));
    }

    /**
     * 新增App端配置信息管理
     */
    @PreAuthorize("@ss.hasPermi('system:appconfig:add')")
    @Log(title = "App端配置信息管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysAppConfig sysAppConfig)
    {
        return toAjax(sysAppConfigService.insertSysAppConfig(sysAppConfig));
    }

    /**
     * 修改App端配置信息管理
     */
    @PreAuthorize("@ss.hasPermi('system:appconfig:edit')")
    @Log(title = "App端配置信息管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysAppConfig sysAppConfig)
    {
        return toAjax(sysAppConfigService.updateSysAppConfig(sysAppConfig));
    }

    /**
     * 删除App端配置信息管理
     */
    @PreAuthorize("@ss.hasPermi('system:appconfig:remove')")
    @Log(title = "App端配置信息管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(sysAppConfigService.deleteSysAppConfigByIds(ids));
    }
}
