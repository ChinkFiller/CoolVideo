package com.test.http_spring.shellCommand;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Help;


@ShellComponent
@ShellCommandGroup("Built-In Commands")
public class OverloadHelpCommand implements Help.Command {
    @ShellMethod("显示可用指令")
    public void help(){
        System.out.println("-----------------------------------------------------");
        System.out.println("stop,quit:停止服务器");
        System.out.println("initdb:初始化数据库，即向数据库插入预制的数据");
        System.out.println("rmuser [用户名]:删除一条用户数据");
        System.out.println("clear: 清屏");
        System.out.println("help: 显示帮助");
        System.out.println("alluser: 显示所有的用户");
        System.out.println("version:查看当前服务端信息");
        System.out.println("bgpage:查看后台管理地址");
        System.out.println("getter --s [start]启动自动爬虫程序\n" +
                           "           [restart]重启自动爬虫\n"+
                           "           [stop]停止自动爬虫\n"+
                           "           [status]查看自动爬虫状态\n"
        );
        System.out.println("-----------------------------------------------------");
    }
}
