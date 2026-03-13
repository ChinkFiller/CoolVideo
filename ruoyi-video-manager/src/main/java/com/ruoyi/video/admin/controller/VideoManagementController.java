package com.ruoyi.video.admin.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.video.admin.domain.form.UpdateConfig;
import com.ruoyi.video.admin.service.VideoManagementService;
import com.ruoyi.video.domain.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 视频数据保存的数据Controller
 *
 * @author ruoyi
 * @date 2025-07-19
 */
@RestController
@RequestMapping("/system/video/film")
public class VideoManagementController extends BaseController
{
    @Autowired
    private VideoManagementService filmDataService;

    /**
     * 查询视频数据保存的数据列表
     */
    @PreAuthorize("@ss.hasPermi('video:data:list')")
    @GetMapping("/list")
    public TableDataInfo list(Video filmData)
    {
        startPage();
        List<Video> list = filmDataService.selectVideoList(filmData);
        return getDataTable(list);
    }

    /**
     * 导出视频数据保存的数据列表
     */
    @PreAuthorize("@ss.hasPermi('video:data:export')")
    @Log(title = "视频数据导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Video filmData)
    {
        List<Video> list = filmDataService.selectVideoList(filmData);
        ExcelUtil<Video> util = new ExcelUtil<Video>(Video.class);
        util.exportExcel(response, list, "视频数据导出");
    }

    /**
     * 获取视频数据保存的数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('video:data:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(filmDataService.selectVideoById(id));
    }


    /**
     * 给视频信息上自动更新锁
     */
    @PreAuthorize("@ss.hasPermi('video:data:edit')")
    @GetMapping(value = "/lock/{id}")
    public AjaxResult lockInfo(@PathVariable("id") Long id)
    {
        return toAjax(filmDataService.lockInfo(id));
    }

    /**
     * 解锁自动更新
     */
    @PreAuthorize("@ss.hasPermi('video:data:edit')")
    @GetMapping(value = "/unlock/{id}")
    public AjaxResult unlockInfo(@PathVariable("id") Long id)
    {
        return toAjax(filmDataService.unLockInfo(id));
    }


    /**
     * 新增视频数据保存的数据
     */
    @PreAuthorize("@ss.hasPermi('video:data:add')")
    @Log(title = "视频数据保存的数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Video filmData)
    {
        return toAjax(filmDataService.insertVideo(filmData));
    }

    /**
     * 修改视频数据保存的数据
     */
    @PreAuthorize("@ss.hasPermi('video:data:edit')")
    @Log(title = "视频数据保存的数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Video filmData)
    {
        return toAjax(filmDataService.updateVideo(filmData));
    }

    /**
     * 删除视频数据保存的数据
     */
    @PreAuthorize("@ss.hasPermi('video:data:remove')")
    @Log(title = "视频数据保存的数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(filmDataService.deleteVideoByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('video:updater:edit')")
    @Log(title = "视频数据自动更新设置", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public AjaxResult update(@RequestBody UpdateConfig config){
        return toAjax(filmDataService.setAutoUpdateConfig(config));
    }


    @PreAuthorize("@ss.hasPermi('video:updater:view')")
    @GetMapping("/update")
    public AjaxResult getUpdateConfig(){
        return AjaxResult.success(filmDataService.getAutoUpdateConfig());
    }


    @PreAuthorize("@ss.hasPermi('video:updater:run')")
    @Log(title = "视频数据自动更新设置", businessType = BusinessType.UPDATE)
    @GetMapping("/update/run")
    public AjaxResult runUpdate(){
        return toAjax(filmDataService.runUpdate());
    }

    @PreAuthorize("@ss.hasPermi('video:data:summry')")
    @GetMapping("/total/info")
    public AjaxResult getTotalInfo(){
        return AjaxResult.success(filmDataService.getTotalInfo());
    }

}
