package com.test.http_spring.Controller;
import com.test.http_spring.service.AdService;
import com.test.http_spring.utils.GlobalValue;
import com.test.http_spring.utils.ToolsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AdController {
    @Autowired
    private AdService adService;

    @GetMapping("/get_banner")
    public Map get_banner(){
        return ToolsFunction.backSuccessDataList(adService.getAllBannerData());
    }

    @GetMapping("/get_fastfunction_set")
    public Map get_fastfunction_set(){
        return ToolsFunction.backSuccessDataList(adService.getAllFastFunctionSetting());
    }

    @GetMapping("/")
    public void myIndex(){
        ToolsFunction.backError(404);
    }

    @GetMapping("/app/version")
    public Map serverVersion(){
        HashMap data=new HashMap();
        data.put("clientVersion", GlobalValue.BootSetting.get("app-version").toString());
        data.put("serverVersion",GlobalValue.version.replace("V",""));
        data.put("url",GlobalValue.BootSetting.get("app-download-url").toString());
        return ToolsFunction.backSuccessDataMap(data);
    }
    @GetMapping("/app/notice")
    public Map getNotice(){
        HashMap data=new HashMap();
        data.put("title",GlobalValue.BootSetting.get("app-notice-title").toString());
        data.put("content",GlobalValue.BootSetting.get("app-notice-content").toString());
        if (GlobalValue.BootSetting.get("app-notice-show").toString().equals("true")){
            data.put("show",true);
        }else {
            data.put("show",false);
        }
        return ToolsFunction.backSuccessDataMap(data);
    }
}
