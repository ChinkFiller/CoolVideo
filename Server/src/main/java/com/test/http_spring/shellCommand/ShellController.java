package com.test.http_spring.shellCommand;


import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class ShellController implements PromptProvider{
    @Override
    public AttributedString getPrompt() {
        // 设置命令提示符文字
        String promot ="\u001B[0;0H> ";
        // 设置命令提示符字体样式
        AttributedStyle promotStyle = AttributedStyle.BOLD.foreground(AttributedStyle.WHITE);
        // 返回命令提示符
        return new AttributedString(promot,promotStyle);
    }

}
