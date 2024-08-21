package com.test.http_spring.shellCommand;

import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Clear;

@ShellComponent
@ShellCommandGroup("Built-In Commands")
public class OverloadClearCommand implements Clear.Command {
    // 使用延迟加载方式注入终端组件
    @Autowired
    @Lazy
    private Terminal terminal;

    @ShellMethod("my clear")
    public void clear(){
        this.terminal.puts(InfoCmp.Capability.clear_screen, new Object[0]);
    }
}
