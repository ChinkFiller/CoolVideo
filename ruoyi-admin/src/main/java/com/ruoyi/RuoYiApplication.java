package com.ruoyi;

import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.web.appWeb.AppWeb;
import com.ruoyi.web.manager.ManagerWebServer;
import org.pf4j.PluginManager;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.Objects;

/**
 * 启动程序
 *
 * @author ruoyi
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableScheduling
public class RuoYiApplication
{
    public static void main(String[] args)
    {
        if (!FileUtils.createBootRequestDir(Arrays.asList("plugins", "imgCache", "logs","upload-temp"))){
            System.exit(0);
        }
        ConfigurableApplicationContext context = SpringApplication.run(RuoYiApplication.class, args);
        try {
            Environment env = context.getEnvironment();
            Integer servicePort = Integer.parseInt(env.getProperty("server.port", "8080"));
            Integer managePort = Integer.parseInt(Objects.requireNonNull(env.getProperty("manager.port","8081")));
            if (Objects.equals(env.getProperty("manager.enabled","true"), "true")){
                System.out.println("==============================================");
                new ManagerWebServer(servicePort,managePort);
                System.out.println("==============================================");
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Web管理后台启动失败,请检查相关配置");
        }

//        try {
//            Environment env = context.getEnvironment();
//            Integer servicePort = Integer.parseInt(env.getProperty("server.port", "8080"));
//            Integer appPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("app.port","8082")));
//            if (Objects.equals(env.getProperty("app.enabled","true"), "true")){
//                System.out.println("==============================================");
//                new AppWeb(servicePort,appPort);
//                System.out.println("==============================================");
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            System.out.println("Web管理后台启动失败,请检查相关配置");
//        }
        System.out.println("""
           _____            _  __      ___     _           \s
          / ____|          | | \\ \\    / (_)   | |          \s
         | |     ___   ___ | |  \\ \\  / / _  __| | ___  ___ \s
         | |    / _ \\ / _ \\| |   \\ \\/ / | |/ _` |/ _ \\/ _ \\\s
         | |___| (_) | (_) | |    \\  /  | | (_| |  __/ (_) |
          \\_____\\___/ \\___/|_|     \\/   |_|\\__,_|\\___|\\___/ \
        """);
    }

    @Bean
    public PluginManager pluginManager(ApplicationContext applicationContext) {
        SpringPluginManager pluginManager = new SpringPluginManager();
        // 让插件能访问 Spring 上下文
        pluginManager.setApplicationContext(applicationContext);

        //插件类加载调试器
        //PluginDebugger.debugExtensions(pluginManager);
        return pluginManager;
    }
}
