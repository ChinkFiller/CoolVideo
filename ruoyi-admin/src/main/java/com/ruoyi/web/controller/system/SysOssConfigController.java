package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysOssConfig;
import com.ruoyi.system.service.ISysOssConfigService;
import com.ruoyi.system.service.ISysOssFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 对象存储配置Controller
 *
 * @author ruoyi
 * @date 2025-10-16
 */
@RestController
@RequestMapping("/system/ossconfig")
public class SysOssConfigController extends BaseController
{
    @Autowired
    private ISysOssConfigService sysOssConfigService;

    @Autowired
    ISysOssFileService sysOssFileService;

    /**
     * 查询对象存储配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:ossconfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysOssConfig sysOssConfig)
    {
        startPage();
        List<SysOssConfig> list = sysOssConfigService.selectSysOssConfigList(sysOssConfig);
        return getDataTable(list);
    }

    /**
     * 导出对象存储配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:ossconfig:export')")
    @Log(title = "对象存储配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysOssConfig sysOssConfig)
    {
        List<SysOssConfig> list = sysOssConfigService.selectSysOssConfigList(sysOssConfig);
        ExcelUtil<SysOssConfig> util = new ExcelUtil<SysOssConfig>(SysOssConfig.class);
        util.exportExcel(response, list, "对象存储配置数据");
    }

    /**
     * 获取对象存储配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:ossconfig:query')")
    @GetMapping(value = "/{ossConfigId}")
    public AjaxResult getInfo(@PathVariable("ossConfigId") Long ossConfigId)
    {
        return success(sysOssConfigService.selectSysOssConfigByOssConfigId(ossConfigId));
    }

    /**
     * 新增对象存储配置
     */
    @PreAuthorize("@ss.hasPermi('system:ossconfig:add')")
    @Log(title = "对象存储配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysOssConfig sysOssConfig)
    {
        return toAjax(sysOssConfigService.insertSysOssConfig(sysOssConfig));
    }

    /**
     * 修改对象存储配置
     */
    @PreAuthorize("@ss.hasPermi('system:ossconfig:edit')")
    @Log(title = "对象存储配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysOssConfig sysOssConfig)
    {
        return toAjax(sysOssConfigService.updateSysOssConfig(sysOssConfig));
    }

    /**
     * 删除对象存储配置
     */
    @PreAuthorize("@ss.hasPermi('system:ossconfig:remove')")
    @Log(title = "对象存储配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ossConfigIds}")
    public AjaxResult remove(@PathVariable Long[] ossConfigIds)
    {
        return toAjax(sysOssConfigService.deleteSysOssConfigByOssConfigIds(ossConfigIds));
    }

    /**
     * 测试对象存储配置
     */
    @PreAuthorize("@ss.hasPermi('system:ossconfig:list')")
    @GetMapping(value = "/test/{ossConfigId}")
    public AjaxResult testConfig(@PathVariable("ossConfigId") Long ossConfigId) {
        sysOssConfigService.testConfig(ossConfigId);
        return AjaxResult.success("测试成功");
    }

    @PreAuthorize("@ss.hasPermi('system:ossconfig:edit')")
    @Log(title = "对象存储配置", businessType = BusinessType.UPDATE)
    @PutMapping("/updateStatus")
    public AjaxResult updateStatus(@RequestBody SysOssConfig sysOssConfig){
        return toAjax(sysOssConfigService.updateStatus(sysOssConfig));
    }

}
