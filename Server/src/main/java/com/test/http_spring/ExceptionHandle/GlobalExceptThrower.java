package com.test.http_spring.ExceptionHandle;


import com.test.http_spring.utils.ToolsFunction;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptThrower {
    @ExceptionHandler(value = RuntimeException.class)
    public void handelExcept(Exception e){
        String msg=e.getMessage();
        if (!(msg.contains("404")||msg.contains("403")||msg.contains("400"))){
            ToolsFunction.errorLog(e.toString());
        }
        ToolsFunction.backError(500);
    }
}
