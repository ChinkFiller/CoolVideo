package com.ruoyi.video.admin.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.video.admin.service.CommonDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


//系统通用字典
@RestController
@RequestMapping("/system/dict")
public class CommonDictController extends BaseController {

    @Autowired
    private CommonDictService commonIndexService;

    @Anonymous
    @GetMapping("/video")
    @PreAuthorize("@ss.hasAnyPermi('video:data:list')")
    public AjaxResult videoInfoDict(){
        return success(commonIndexService.selectVideoInfoDict());
    }

    @Anonymous
    @GetMapping("/user")
    @PreAuthorize("@ss.hasAnyPermi('system:user:list')")
    public AjaxResult userInfoDict(){
        return success(commonIndexService.selectUserInfoDict());
    }
}
