package com.ruoyi.video.admin.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.video.admin.domain.form.SystemCommonSettingForm;
import com.ruoyi.video.admin.domain.vo.SystemCommonSettingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/system/common/settings")
public class SystemCommonSettingsController extends BaseController {

    @Autowired
    ISysConfigService configService;


    @PreAuthorize("@ss.hasAnyPermi('system:settings:list')")
    @GetMapping("/config")
    public AjaxResult config() {
        return success(SystemCommonSettingVo.builder()
                .captchaEnabled(configService.selectCaptchaEnabled())
                .registerEnabled(configService.selectConfigByKey("sys.account.registerUser").equals("true"))
                .cacheImgNum(FileUtils.getDirFileNum("imgCache"))
                .cacheImgSize(FileUtils.getDirFileSize("imgCache"))
                .build());
    }


    @PreAuthorize("@ss.hasAnyPermi('system:settings:edit')")
    @PostMapping("/submit")
    public AjaxResult CaptchaSwitch(
            @RequestBody SystemCommonSettingForm form
    ) {
        // 获取配置信息
        SysConfig captchaConfig=configService.selectFullConfigByKey("sys.account.captchaEnabled");
        SysConfig registerConfig=configService.selectFullConfigByKey("sys.account.registerUser");

        // 设置配置信息
        captchaConfig.setConfigValue(form.getCaptchaEnabled()?"true":"false");
        registerConfig.setConfigValue(form.getRegisterEnabled()?"true":"false");

        // 修改配置信息
        configService.updateConfig(captchaConfig);
        configService.updateConfig(registerConfig);

        return success();
    }


    @PreAuthorize("@ss.hasAnyPermi('system:settings:edit')")
    @DeleteMapping("/cache/img")
    public AjaxResult clearImgCache() {
        return toAjax(FileUtils.clearDir("imgCache"));
    }



}
