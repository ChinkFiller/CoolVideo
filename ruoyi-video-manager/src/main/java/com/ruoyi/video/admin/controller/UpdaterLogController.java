package com.ruoyi.video.admin.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.video.admin.service.UpdaterLogService;
import com.ruoyi.video.domain.UpdaterLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 更新器日志Controller
 *
 * @author ruoyi
 * @date 2025-08-09
 */
@RestController
@RequestMapping("/system/video/updater/log")
public class UpdaterLogController extends BaseController
{
    @Autowired
    private UpdaterLogService updaterLogService;

    /**
     * 查询更新器日志列表
     */
    @PreAuthorize("@ss.hasPermi('video:log:list')")
    @GetMapping("/list")
    public TableDataInfo list(UpdaterLog updaterLog)
    {
        startPage();
        List<UpdaterLog> list = updaterLogService.selectUpdaterLogList(updaterLog);
        return getDataTable(list);
    }

    /**
     * 导出更新器日志列表
     */
    @PreAuthorize("@ss.hasPermi('video:log:export')")
    @Log(title = "更新器日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UpdaterLog updaterLog)
    {
        List<UpdaterLog> list = updaterLogService.selectUpdaterLogList(updaterLog);
        ExcelUtil<UpdaterLog> util = new ExcelUtil<UpdaterLog>(UpdaterLog.class);
        util.exportExcel(response, list, "更新器日志数据");
    }

    @PreAuthorize("@ss.hasPermi('video:log:remove')")
    @Log(title = "更新器日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/all")
    public AjaxResult removeAll()
    {
        return toAjax(updaterLogService.deleteAllUpdaterLog());
    }
}
