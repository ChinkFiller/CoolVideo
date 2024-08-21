package com.test.http_spring.Controller;


import com.test.http_spring.service.WeekService;
import com.test.http_spring.utils.ToolsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class WeekdataController {
    @Autowired
    WeekService weekService;
    @GetMapping("/get_weekdata")
    public java.util.Map get_week(){
        HashMap backdatas=new HashMap();
        for (int i=0;i<7;i++){
            backdatas.put(String.valueOf(i+1),weekService.findAllWeekData(String.valueOf(i)));
        }
        return ToolsFunction.backSuccessDataMap(backdatas);
    }
}
