package com.test.http_spring.shellCommand;

import com.test.http_spring.HttpSpringApplication;
import com.test.http_spring.utils.GlobalValue;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Version;


@ShellComponent
@ShellCommandGroup("Built-In Commands")
public class OverloadVersionCommand implements Version.Command {
    @ShellMethod
    public void version(){
        System.out.println("   _____            _  __      ___     _            \n" +
                "  / ____|          | | \\ \\    / (_)   | |           \n" +
                " | |     ___   ___ | |  \\ \\  / / _  __| | ___  ___  \n" +
                " | |    / _ \\ / _ \\| |   \\ \\/ / | |/ _` |/ _ \\/ _ \\ \n" +
                " | |___| (_) | (_) | |    \\  /  | | (_| |  __/ (_) |\n" +
                "  \\_____\\___/ \\___/|_|     \\/   |_|\\__,_|\\___|\\___/ ");
        System.out.println();
        System.out.println();
        System.out.println("-----------------------------------------------------");
        System.out.println("Version:"+ GlobalValue.version);
        System.out.println("Build:conyafer");
        System.out.println("-----------------------------------------------------");
    }
}
