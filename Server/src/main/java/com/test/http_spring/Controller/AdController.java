package com.test.http_spring.Controller;
import com.test.http_spring.service.AdService;
import com.test.http_spring.utils.ToolsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
