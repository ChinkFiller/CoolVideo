package com.ruoyi.web.controller.common;


import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.ResultCodes;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.SysAppConfig;
import com.ruoyi.system.service.ISysAppConfigService;
import com.ruoyi.video.domain.vo.AppConfigVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/common")
@RestController
@Tag(description = "用于获取服务器版本，系统更新，通用查询", name ="公共信息获取模块")
public class ApplicationConfigController extends BaseController {

    @Autowired
    ISysAppConfigService appConfigService;

    @Anonymous
    @GetMapping("/client/config")
    @Operation(summary = "获取客户端配置")
    public AjaxResult clientConfig2(
            @RequestParam(value = "version") Integer version,
            @RequestParam(value = "plt") String plt,
            @RequestParam(value = "appid") String appID
    ){
        return AjaxResult.success(this.getClientInfo(appID,version,plt));
    }



    private AppConfigVo getClientInfo(String appID,Integer version,String plt) {
        //查询App最新版的信息
        SysAppConfig appConfig=appConfigService.getAppUpdateInfo(
                // 平台代号可能包含大写，全部转化为小写
                SysAppConfig.builder().appId(appID).plt(plt.toLowerCase()).build()
        );

        //判断App是否存在
        if (appConfig == null) {
            throw new ServiceException(ResultCodes.APP_NOT_EXIST_ERROR);
        }

        if (appConfig.getState().equals("0")){
            throw new ServiceException(ResultCodes.APP_STOP_ERROR);
        }

        return AppConfigVo
                .builder()
                .lastVersion(appConfig.getAppVersion())
                .plt(appConfig.getPlt())
                .UpdateUrl(appConfig.getUpdateUrl())
                .newVersionDesc(appConfig.getDescription())
                .hasUpdate(version<appConfig.getAppVersion())
                //是否可忽略
                //开启强制更新，强制更新，不可忽略。低于最低支持版本，不可忽略
                .ignorable(!((appConfig.getForceUpdate().equals("1") && version<appConfig.getAppVersion()) || version<appConfig.getFinalVersion()))
                .build();
    }


}
