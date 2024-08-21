package com.test.http_spring.shellCommand;


import com.test.http_spring.utils.GlobalValue;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Quit;

@ShellComponent
@ShellCommandGroup("Built-In Commands")
public class OverloadExitCommand implements Quit.Command{
    @ShellMethod()
    public void quit(){
        if (GlobalValue.autoGetterState.equals("running")||GlobalValue.autoGetterState.equals("retrying")){
            GlobalValue.autoGetterState="stopping";
            GlobalValue.Getter.destroy();
        }
        System.exit(0);
    }
}
