package com.ruoyi.video.admin.controller;


import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.service.ISysDictDataService;
import com.ruoyi.video.admin.domain.vo.VideoTagVo;
import com.ruoyi.video.admin.mapper.VideoManagementMapper;
import com.ruoyi.video.admin.service.VideoTagService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/video/tag")
public class VideoTagController extends BaseController {

    @Autowired
    private ISysDictDataService dictDataService;

    @Autowired
    VideoTagService videoTagService;


    @PreAuthorize("@ss.hasPermi('video:tag:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysDictData data) {
        data.setDictType("video_tag");
        startPage();
        List<SysDictData> list = dictDataService.selectDictDataList(data);
        return getDataTable(list);
    }


    @PreAuthorize("@ss.hasPermi('video:tag:query')")
    @GetMapping(value = "/{dictCode}")
    public AjaxResult getInfo(
            @PathVariable Long dictCode
    ){
        return success(videoTagService.getVideoTagInfo(dictCode));
    }

    @PreAuthorize("@ss.hasPermi('video:tag:add')")
    @PostMapping
    public AjaxResult add(
            @Validated @RequestBody SysDictData dict
    ){
        return toAjax(videoTagService.addTag(dict));
    }


    @PreAuthorize("@ss.hasPermi('video:tag:edit')")
    @PutMapping
    public AjaxResult edit(
            @Validated @RequestBody VideoTagVo dict
    ) {
       return toAjax(videoTagService.updateTag(dict));
    }

    @PreAuthorize("@ss.hasPermi('video:tag:remove')")
    @DeleteMapping("/{dictCodes}")
    public AjaxResult remove(@PathVariable Long[] dictCodes)
    {
        return toAjax(videoTagService.deleteTag(dictCodes));
    }
}
