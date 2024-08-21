package com.test.http_spring.EventHandel;

import com.test.http_spring.utils.GlobalValue;
import com.test.http_spring.utils.ToolsFunction;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@Component
public class StopEventHandel{
    @PreDestroy
    public void handelStopEvent(){
        ToolsFunction.infoLog("服务器正在停止...");
        //停止自动爬虫程序
        if (!(GlobalValue.autoGetterState.equals("running")||GlobalValue.autoGetterState.equals("retrying"))){
            //终止爬虫程序
            ToolsFunction.terminateGetter();
        }
    }
}
