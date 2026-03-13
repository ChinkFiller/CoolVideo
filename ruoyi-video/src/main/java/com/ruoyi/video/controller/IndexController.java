package com.ruoyi.video.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.video.service.IndexService;
import com.ruoyi.video.service.WeekService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
@Tag(name = "首页",description = "首页相关接口")
public class IndexController extends BaseController {

    @Autowired
    IndexService indexService;

    @Autowired
    WeekService weekService;


    @Anonymous
    @GetMapping("/config")
    @Operation(summary = "获取首页配置信息")
    public AjaxResult getConfig(){
        return AjaxResult.success(indexService.getIndexInfo());
    }

    @Anonymous
    @GetMapping("/weekTable")
    @Operation(summary = "获取周期表")
    public AjaxResult getWeekTable(){
        return AjaxResult.success(weekService.getWeekVideo());
    }
}
