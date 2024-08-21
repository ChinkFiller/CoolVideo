package com.test.http_spring.BootSetter;

import com.test.http_spring.utils.GlobalValue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class BootSetter implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String name = "Config resource 'file [application.properties]' via location 'optional:file:./'";
        MapPropertySource propertySource = (MapPropertySource) environment.getPropertySources().get(name);
        Map<String, Object> source = propertySource.getSource();
        Map ordata = new HashMap();
        Map newdata=new HashMap();
        source.forEach((k, v) -> {
            ordata.put(k , v);
        });
        //端口不填默认为8080
        newdata.put("server.port",ordata.get("port")==null?8080:ordata.get("port"));
        //服务器的设置
        if (ordata.get("database-type").toString().equals("sqlite")){
            newdata.put("spring.datasource.driver-class-name","org.sqlite.JDBC");
            newdata.put("spring.datasource.url","jdbc:sqlite:"+ordata.get("database-position"));
        }
        if (ordata.get("database-type").toString().equals("mysql")){
            newdata.put("spring.datasource.driver-class-name","com.mysql.cj.jdbc.Driver");
            newdata.put("spring.datasource.url","jdbc:mysql://"+ordata.get("database-position")+"/"+ordata.get("database-name")+"?serverTimeZone=UTC&characterEncoding=utf8&useUnicode=true&useSSL=false");
            newdata.put("spring.datasource.username",ordata.get("database-username"));
            newdata.put("spring.datasource.password",ordata.get("database-password"));
        }
        if (ordata.get("mail-host").toString().split(":").length==1){
            newdata.put("spring.mail.host",ordata.get("mail-host"));
            newdata.put("spring.mail.port",465);
        }else{
            newdata.put("spring.mail.host",ordata.get("mail-host").toString().split(":")[0]);
            newdata.put("spring.mail.port",Integer.parseInt(ordata.get("mail-host").toString().split(":")[1]));
        }
        //邮箱系统的设置
        newdata.put("spring.mail.properties.mail.debug",ordata.get("mail-debug"));
        newdata.put("spring.mail.username",ordata.get("mail-username"));
        newdata.put("spring.mail.password",ordata.get("mail-password"));
        newdata.put("spring.mail.protocol","smtp");
        //邮件的设置
        newdata.put("spring.mail.properties.mail.smtp.ssl.enable","true");
        newdata.put("spring.mail.properties.mail.smtp.socketFactory.port",465);
        newdata.put("spring.mail.default-encoding","UTF-8");
        newdata.put("spring.mail.properties.mail.smtp.socketFactoryClass","javax.net.ssl.SSLSocketFactory");
        //日志输出，为关闭所有的日志输出
        newdata.put("logging.level.root","info");
        //配置mybaites的脚本位置
        newdata.put("mybatis.mapper-locations", "classpath*:*.xml");
        //配置初始化建表sql脚本
        if (ordata.get("database-type").toString().equals("sqlite")){
            newdata.put("spring.sql.init.schema-locations","classpath:sql/sqliteinit.sql");
        }
        if (ordata.get("database-type").toString().equals("mysql")){
            newdata.put("spring.sql.init.schema-locations","classpath:sql/mysqlinit.sql");
        }
        newdata.put("spring.sql.init.mode","always");
        //关闭管理端口
        newdata.put("management.server.port","-1");
        newdata.put("web.resources.static-locations","classpath:/static");
        newdata.put("spring.mvc.static-path-pattern","/static/**");
        newdata.put("server.error.whitelabel.enabled","false");
        newdata.put("spring.servlet.multipart.enabled","true");
        newdata.put("spring.servlet.multipart.max-file-size","300MB");
        newdata.put("spring.servlet.multipart.max-request-size","300MB");
        GlobalValue.BootSetting=ordata;
        environment.getPropertySources().replace(name, new MapPropertySource(name, newdata));
    }
}
