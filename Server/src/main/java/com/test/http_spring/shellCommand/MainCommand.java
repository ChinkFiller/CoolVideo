package com.test.http_spring.shellCommand;

import com.test.http_spring.pojo.users;
import com.test.http_spring.service.AdService;
import com.test.http_spring.service.FilmService;
import com.test.http_spring.service.UserService;
import com.test.http_spring.service.WeekService;
import com.test.http_spring.utils.GlobalValue;
import com.test.http_spring.utils.ToolsFunction;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import javax.sql.DataSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@ShellComponent
@ShellCommandGroup("Built-In Commands")
public class MainCommand {
    @Autowired
    FilmService filmService;
    @Autowired
    UserService userService;
    @Autowired
    WeekService weekService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    AdService adService;

    @ShellMethod("")
    public String initdb(){
        System.out.println("正在初始化数据库");
        //通过数据源获取数据库链接
        Connection connection = DataSourceUtils.getConnection(dataSource);
        //创建脚本执行器
        System.out.println("-----------------------------------------------------");
        if (filmService.findAllFilm().isEmpty()){
            try {
                ScriptRunner scriptRunner = new ScriptRunner(connection);
                StringWriter writer = new StringWriter();
                PrintWriter print = new PrintWriter(writer);
                scriptRunner.setLogWriter(print);
                scriptRunner.setStopOnError(false);
                Reader fmreader = Resources.getResourceAsReader("sql/filminit.sql");
                scriptRunner.runScript(fmreader);
                fmreader.close();
                System.out.println("视频数据初始化成功");
            }catch (RuntimeException | IOException e){
                System.out.println("视频数据初始化失败");
            }
        }
        System.out.println("-----------------------------------------------------");
        if (userService.findAllUser().isEmpty()){
            try {
                ScriptRunner scriptRunner = new ScriptRunner(connection);
                StringWriter writer = new StringWriter();
                PrintWriter print = new PrintWriter(writer);
                scriptRunner.setLogWriter(print);
                scriptRunner.setStopOnError(false);
                Reader usreader = Resources.getResourceAsReader("sql/userinit.sql");
                scriptRunner.runScript(usreader);
                usreader.close();
                System.out.println("用户数据初始化成功");
            }catch (RuntimeException | IOException e){
                System.out.println("用户数据初始化失败");
            }
        }
        System.out.println("-----------------------------------------------------");
        if (weekService.findAllWeekDataNoDay().isEmpty()) {
            try{
                ScriptRunner scriptRunner = new ScriptRunner(connection);
                StringWriter writer = new StringWriter();
                PrintWriter print = new PrintWriter(writer);
                scriptRunner.setLogWriter(print);
                scriptRunner.setStopOnError(false);
                Reader wkreader = Resources.getResourceAsReader("sql/weekinit.sql");
                scriptRunner.runScript(wkreader);
                wkreader.close();
                System.out.println("周期表数据初始化成功");
            }catch (RuntimeException | IOException e){
                System.out.println("周期表数据初始化失败");
            }
        }
        System.out.println("-----------------------------------------------------");
        if (adService.getAllBannerData().isEmpty()) {
            try{
                ScriptRunner scriptRunner = new ScriptRunner(connection);
                StringWriter writer = new StringWriter();
                PrintWriter print = new PrintWriter(writer);
                scriptRunner.setLogWriter(print);
                scriptRunner.setStopOnError(false);
                Reader wkreader = Resources.getResourceAsReader("sql/bannerinit.sql");
                scriptRunner.runScript(wkreader);
                wkreader.close();
                System.out.println("Banner数据初始化成功");
            }catch (RuntimeException | IOException e){
                System.out.println("Banner数据初始化失败");
            }
        }
        System.out.println("-----------------------------------------------------");
        if (adService.getAllFastFunctionSetting().isEmpty()) {
            try{
                ScriptRunner scriptRunner = new ScriptRunner(connection);
                StringWriter writer = new StringWriter();
                PrintWriter print = new PrintWriter(writer);
                scriptRunner.setLogWriter(print);
                scriptRunner.setStopOnError(false);
                Reader wkreader = Resources.getResourceAsReader("sql/fastfunctioninit.sql");
                scriptRunner.runScript(wkreader);
                wkreader.close();
                System.out.println("快速功能数据初始化成功");
            }catch (RuntimeException | IOException e){
                System.out.println("快速功能数据初始化失败");
            }
        }
        System.out.println("-----------------------------------------------------");
        return "数据库初始化成功";
    }
    @ShellMethod()
    public void stop(){
        if (GlobalValue.autoGetterState.equals("running")||GlobalValue.autoGetterState.equals("retrying")){
            ToolsFunction.terminateGetter();
        }
        System.exit(0);
    }
    @ShellMethod()
    public String rmuser(String username){
        if (userService.getOneUserByName(username)==null){
            return "[Error]用户"+username+"不存在";
        }else {
            userService.removeOneUser(username);
            return "用户"+username+"删除成功";
        }
    }

    @ShellMethod
    public void alluser(){
        List<users> userdata=userService.findAllUser();
        System.out.println("----------------------------------------------------------------------");
        System.out.println("%-18s%-18s%-18s%-18s".formatted("Username","Password","EmailAddress","Permission"));
        for (int i=0;i<userdata.size();i++){
            System.out.println("%-18s%-18s%-18s%-18s".formatted(userdata.get(i).getUser(),userdata.get(i).getPawd(),userdata.get(i).getUser(),userdata.get(i).getVip()));
        }
        System.out.println("----------------------------------------------------------------------");
    }
    @ShellMethod
    public void bgpage(){
        System.out.println("-----------------------------------------------------");
        System.out.println("后台地址:/CoolVideoAdmin/index/"+ GlobalValue.randomAdminPath);
        System.out.println("-----------------------------------------------------");
    }
    @ShellMethod
    public void getter(String s){
        if (s.equals("start")||s.equals("restart")){
            filmService.bootAutoGetter();
            return;
        }
        if (s.equals("status")){
            System.out.println("自动程序运行状态:"+GlobalValue.autoGetterState);
            return;
        }
        //Getter终止命令
        if (s.equals("stop")){
            //检查getter状态，如果是非运行状态，不设置信号
            if (!(GlobalValue.autoGetterState.equals("running")||GlobalValue.autoGetterState.equals("retrying"))){
                System.out.println("自动程序暂未启动");
                return;
            }else{
                ToolsFunction.terminateGetter();
                System.out.println("自动程序已停止");
                return;
            }
        }
        System.out.println("未找到参数 "+s);
    }
}
