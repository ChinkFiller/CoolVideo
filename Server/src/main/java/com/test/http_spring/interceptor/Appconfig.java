package com.test.http_spring.interceptor;
import com.test.http_spring.utils.GlobalValue;
import com.test.http_spring.utils.ToolsFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableAsync
@EnableScheduling
@EnableWebSocket
public class Appconfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //创建拦截器类
        HandlerInterceptor interceptor=new Ipcheckhandle();
        //白名单
        List<String> patterns=new ArrayList<>();
//        patterns.add("/CoolVideoAdmin/index/"+GlobalValue.randomAdminPath+"/**");
//        patterns.add("/CoolVideoAdmin/api/"+GlobalValue.randomAdminPath+"/**");
//        registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns(patterns);
        ToolsFunction.infoLog("服务器已启动");
        ToolsFunction.infoLog("版本:"+ GlobalValue.version);
    }

    @Bean
    public ServerEndpointExporter serverEndpoint() {
        return new ServerEndpointExporter();
    }
}
