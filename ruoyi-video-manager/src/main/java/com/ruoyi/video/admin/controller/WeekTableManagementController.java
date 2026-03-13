package com.ruoyi.video.admin.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.video.admin.service.WeekManagementService;
import com.ruoyi.video.domain.WeekTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 视频周期Controller
 *
 * @author ruoyi
 * @date 2025-08-07
 */
@RestController
@RequestMapping("/system/video/week")
public class WeekTableManagementController extends BaseController
{
    @Autowired
    private WeekManagementService weekService;

    /**
     * 查询视频周期列表
     */
    @PreAuthorize("@ss.hasPermi('video:week:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeekTable week)
    {
        startPage();
        List<WeekTable> list = weekService.selectWeekList(week);
        return getDataTable(list);
    }

    /**
     * 导出视频周期列表
     */
    @PreAuthorize("@ss.hasPermi('video:week:export')")
    @Log(title = "视频周期", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeekTable week)
    {
        List<WeekTable> list = weekService.selectWeekList(week);
        ExcelUtil<WeekTable> util = new ExcelUtil<WeekTable>(WeekTable.class);
        util.exportExcel(response, list, "视频周期数据");
    }

    /**
     * 获取视频周期详细信息
     */
    @PreAuthorize("@ss.hasPermi('video:week:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weekService.selectWeekById(id));
    }

    /**
     * 新增视频周期
     */
    @PreAuthorize("@ss.hasPermi('video:week:add')")
    @Log(title = "视频周期", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeekTable week)
    {
        return toAjax(weekService.insertWeek(week));
    }

    /**
     * 修改视频周期
     */
    @PreAuthorize("@ss.hasPermi('video:week:edit')")
    @Log(title = "视频周期", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeekTable week)
    {
        return toAjax(weekService.updateWeek(week));
    }

    /**
     * 删除视频周期
     */
    @PreAuthorize("@ss.hasPermi('video:week:remove')")
    @Log(title = "视频周期", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weekService.deleteWeekByIds(ids));
    }
}
