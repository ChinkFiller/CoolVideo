package com.ruoyi.video.admin.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.ip.AddressUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.video.admin.service.FastFunctionManagementService;
import com.ruoyi.video.domain.FastFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 主页快速功能Controller
 *
 * @author ruoyi
 * @date 2025-08-07
 */
@RestController
@RequestMapping("/system/video/function")
public class FastFunctionManagementController extends BaseController
{
    @Autowired
    private FastFunctionManagementService fastFunctionService;

    /**
     * 查询主页快速功能列表
     */
    @PreAuthorize("@ss.hasPermi('video:function:list')")
    @GetMapping("/list")
    public TableDataInfo list(FastFunction fastFunction)
    {
        startPage();
        List<FastFunction> list = fastFunctionService.selectFastFunctionList(fastFunction);
        return getDataTable(list);
    }

    /**
     * 导出主页快速功能列表
     */
    @PreAuthorize("@ss.hasPermi('video:function:export')")
    @Log(title = "主页快速功能", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FastFunction fastFunction)
    {
        List<FastFunction> list = fastFunctionService.selectFastFunctionList(fastFunction);
        ExcelUtil<FastFunction> util = new ExcelUtil<FastFunction>(FastFunction.class);
        util.exportExcel(response, list, "主页快速功能数据");
    }

    /**
     * 获取主页快速功能详细信息
     */
    @PreAuthorize("@ss.hasPermi('video:function:query')")
    @GetMapping(value = "/{fid}")
    public AjaxResult getInfo(@PathVariable("fid") Long fid)
    {
        System.out.println(AddressUtils.getRealAddressByIP("113.186.11.36"));
        return success(fastFunctionService.selectFastFunctionByFid(fid));
    }

    /**
     * 新增主页快速功能
     */
    @PreAuthorize("@ss.hasPermi('video:function:add')")
    @Log(title = "主页快速功能", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FastFunction fastFunction)
    {
        return toAjax(fastFunctionService.insertFastFunction(fastFunction));
    }

    /**
     * 修改主页快速功能
     */
    @PreAuthorize("@ss.hasPermi('video:function:edit')")
    @Log(title = "主页快速功能", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FastFunction fastFunction)
    {
        return toAjax(fastFunctionService.updateFastFunction(fastFunction));
    }

    /**
     * 删除主页快速功能
     */
    @PreAuthorize("@ss.hasPermi('video:function:remove')")
    @Log(title = "主页快速功能", businessType = BusinessType.DELETE)
    @DeleteMapping("/{fids}")
    public AjaxResult remove(@PathVariable Long[] fids)
    {
        return toAjax(fastFunctionService.deleteFastFunctionByFids(fids));
    }
}
