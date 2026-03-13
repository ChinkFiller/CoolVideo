package com.ruoyi.video.admin.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.video.admin.domain.VideoStorage;
import com.ruoyi.video.admin.domain.form.VideoStorageUploadForm;
import com.ruoyi.video.admin.service.VideoStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping("/system/video/storage")
public class VideoStorageController extends BaseController {

    @Autowired
    private VideoStorageService videoStorageService;

    /**
     * 查询视频资源信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:videoStorage:list')")
    @GetMapping("/list")
    public TableDataInfo list(VideoStorage VideoStorage)
    {
        startPage();
        List<VideoStorage> list = videoStorageService.selectVideoStorageList(VideoStorage);
        return getDataTable(list);
    }

    /**
     * 导出视频资源信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:videoStorage:export')")
    @Log(title = "视频资源信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, VideoStorage VideoStorage)
    {
        List<VideoStorage> list = videoStorageService.selectVideoStorageList(VideoStorage);
        ExcelUtil<VideoStorage> util = new ExcelUtil<VideoStorage>(VideoStorage.class);
        util.exportExcel(response, list, "视频资源信息数据");
    }

    /**
     * 获取视频资源信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:videoStorage:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(videoStorageService.selectVideoStorageById(id));
    }

    /**
     * 新增视频资源信息
     */
    @PreAuthorize("@ss.hasPermi('system:videoStorage:add')")
    @Log(title = "视频资源信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(VideoStorageUploadForm form)
    {
        return toAjax(videoStorageService.insertVideoStorage(form));
    }

    /**
     * 修改视频资源信息
     */
    @PreAuthorize("@ss.hasPermi('system:videoStorage:edit')")
    @Log(title = "视频资源信息", businessType = BusinessType.UPDATE)
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult edit(@ModelAttribute VideoStorageUploadForm form)
    {
        return toAjax(videoStorageService.updateVideoStorage(form));
    }

    /**
     * 删除视频资源信息
     */
    @PreAuthorize("@ss.hasPermi('system:videoStorage:remove')")
    @Log(title = "视频资源信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(videoStorageService.deleteVideoStorageByIds(ids));
    }

}
