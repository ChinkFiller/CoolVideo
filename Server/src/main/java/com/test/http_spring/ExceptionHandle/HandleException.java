package com.test.http_spring.ExceptionHandle;



import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HandleException implements ErrorController {
    @RequestMapping("/error")
    public void handleError(HttpServletResponse response) throws IOException {
        Map<String, Object> errorAttributes = new HashMap<>();
        if (response.getStatus() == 400) {
            errorAttributes.put("msg", "发送的参数有问题");
            errorAttributes.put("code", "400");
            errorAttributes.put("error", 1);
        } else if (response.getStatus() == 405) {
            errorAttributes.put("msg", "访问方法有问题捏");
            errorAttributes.put("code", "405");
            errorAttributes.put("error", 1);
        } else if (response.getStatus() == 404) {
            errorAttributes.put("msg", "木有找到~");
            errorAttributes.put("code", "404");
            errorAttributes.put("error", 1);
        } else if (response.getStatus() == 500) {
            errorAttributes.put("msg", "服务器扛下了太多T_T");
            errorAttributes.put("code", "500");
            errorAttributes.put("error", 1);
        } else if (response.getStatus() == 403) {
            errorAttributes.put("msg", "没钥匙还想进家门?");
            errorAttributes.put("code", "403");
            errorAttributes.put("error", 1);
        } else {
            errorAttributes.put("msg", "未知错误");
            errorAttributes.put("code", response.getStatus());
            errorAttributes.put("error", 1);
        }
        JSONObject jsonObject=new JSONObject(errorAttributes);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(jsonObject.toString());
    }
}
