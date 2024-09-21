package com.test.http_spring;


import com.test.http_spring.service.FilmService;
import com.test.http_spring.utils.GlobalValue;
import com.test.http_spring.utils.ToolsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Properties;

@SpringBootApplication
@AutoConfiguration("com.test.http_spring.mapper.*")
public class HttpSpringApplication {
    public static void main(String[] args) {
        File setting=new File("application.properties");
        String[] set ={
                "mail-password",
                "mail-username",
                "mail-host",
                "database-position",
                "mail-debug",
                "database-type"
        };
        if (!setting.exists()){
            String data= """
                    #服务器的端口
                    port=9008

                    #应用的名称，用于发送邮件使用
                    app-name="CoolVideo"
                    #tip，用于在邮件模板上加上提示词的
                    tips="CoolVideo 一款开源的视频app模板"

                    #数据库服务器类型，可选sqlite和mysql
                    database-type=sqlite
                    #数据库地址，sqlite的时候填文件名即可，mysql时ip:端口号
                    database-position=test.db

                    #mysql服务器时必填项
                    database-username=
                    database-password=
                    database-name=

                    #邮箱系统
                    #目前仅支持smtp协议
                    #ip:端口,一般为465
                    mail-host=smtp.qq.com:465
                    #邮箱账号
                    mail-username=
                    #邮箱的授权码
                    mail-password=
                    #邮箱的debug开关.开启后可以查看邮件发送状态
                    mail-debug=false
                    #访问速率限制,取值0.2-1
                    speed-limit=1
                    """;
            try {
                FileWriter fw=new FileWriter(setting.getName(),StandardCharsets.UTF_8);
                fw.write(data);
                fw.close();
                ToolsFunction.infoLog("配置文件已生成，请修改文件后重启服务器");
                System.exit(0);
            }catch (IOException e){
                ToolsFunction.errorLog("配置文件初始化，检查权限后重试");
                System.exit(0);
            }
        }else{
            try {
                Properties properties = new Properties();
                BufferedReader bufferedReader = new BufferedReader(new FileReader("application.properties"));
                properties.load(bufferedReader);
                for (String s : set) {//用于检查系统的配置文件
                    if (properties.get(s) == null) {
                        ToolsFunction.errorLog("系统启动配置项不全，请检查后重启");
                        ToolsFunction.errorLog("缺少" + s + "选项");
                        System.exit(0);
                    }
                    if (s.equals("database-type")) {
                        if (!properties.get("database-type").equals("sqlite") && !properties.get("database-type").equals("mysql")) {
                            ToolsFunction.errorLog("不受支持的数据库类型");
                            System.exit(0);
                        }
                    }
                }
                //加载拦截关键词，以逗号分割
                File badWords=new File("badWord.txt");
                if (!badWords.exists()){
                    if (!badWords.createNewFile()){
                        ToolsFunction.warningLog("创建拦截关键词失败");
                    }
                }else{
                    if (Files.size(Path.of("badWord.txt"))!=0){
                        String content = new String(Files.readAllBytes(Paths.get("badWord.txt")));
                        GlobalValue.badWords.addAll(Arrays.asList(content.split(",")));
                    }
                }
                properties.putIfAbsent("port", 8080);
                System.out.println(ToolsFunction.colorFont("""
                           _____            _  __      ___     _           \s
                          / ____|          | | \\ \\    / (_)   | |          \s
                         | |     ___   ___ | |  \\ \\  / / _  __| | ___  ___ \s
                         | |    / _ \\ / _ \\| |   \\ \\/ / | |/ _` |/ _ \\/ _ \\\s
                         | |___| (_) | (_) | |    \\  /  | | (_| |  __/ (_) |
                          \\_____\\___/ \\___/|_|     \\/   |_|\\__,_|\\___|\\___/ \
                        """,35));
                System.out.println();
                System.out.println();
                try {
                    HashMap<String,String> data=new HashMap<>();
                    data.put("tips",properties.get("tips").toString());
                    data.put("app-name",properties.get("app-name").toString());
                    GlobalValue.emailData=data;
                }catch (Exception e){
                    ToolsFunction.warningLog("未设置提示词或应用名，将使用默认配置");
                }
                try {
                    GlobalValue.speedLimite=Float.parseFloat(properties.get("speed-limit").toString());
                    if (GlobalValue.speedLimite>1.0||GlobalValue.speedLimite<0.2){
                        ToolsFunction.warningLog("访问限制数值太大或太小，将使用默认值0.8");
                    }
                }catch (Exception e){
                    GlobalValue.speedLimite=(float)0.8;
                    ToolsFunction.warningLog("未配置访问速率限制，将使用默认值0.8");
                }
                File videoFiles=new File("video");
                if (!videoFiles.exists()){
                    if (!videoFiles.mkdir()){
                        GlobalValue.canUserCdn=false;
                        ToolsFunction.warningLog("视频缓存文件夹创建失败，请稍后重试");
                    }
                }
                if (!(videoFiles.canRead()&&videoFiles.canWrite())){
                    ToolsFunction.warningLog("视频缓存系统出错，本地视频缓存功能将不可用");
                    GlobalValue.canUserCdn=false;
                }
                ToolsFunction.infoLog("服务设置端口:"+properties.get("port"));
                ToolsFunction.infoLog("已设置数据库地址:"+properties.get("database-position"));
                ToolsFunction.infoLog("已设置邮箱服务器地址:"+properties.get("mail-host"));
                ToolsFunction.infoLog("已设置邮箱账户:"+properties.get("mail-username"));
                SpringApplication springApplication = new SpringApplication(HttpSpringApplication.class);
                //关闭spring原始的banner
                springApplication.setBannerMode(Banner.Mode.OFF);
                springApplication.run(args);
            }catch (IOException e){
                ToolsFunction.errorLog("配置文件读取失败,请检查权限后使用");
                System.exit(0);
            }
        }

    }
}
