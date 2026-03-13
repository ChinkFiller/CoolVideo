package com.ruoyi.common.utils.mail;


import com.ruoyi.common.enums.ResultCodes;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.InternetAddress;
import java.io.File;
import java.util.Date;
import java.util.List;

@Component
public class MailUtils {


    @Value("${ruoyi.name}")
    private String title;

    @Autowired
    private JavaMailSender javaMailSender;//注入邮件工具类


    public void send(String name, String form, String to, String subject, String content, Boolean isHtml, String cc, String bcc, List<File> files) {

        if (StringUtils.isAnyBlank(form, to, subject, content)) {
            throw new RuntimeException(ResultCodes.MAIL_PARAMA_ERROR.getMsg());
        }
        try {
            //true表示支持复杂类型
            MimeMessageHelper messageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage(), true);
            //邮件发信人
            messageHelper.setFrom(new InternetAddress(name + "<" + form + ">"));
            //邮件收信人
            messageHelper.setTo(to.split(","));
            //邮件主题
            messageHelper.setSubject(subject);
            //邮件内容
            messageHelper.setText(content, isHtml);
            //抄送
            if (!StringUtils.isEmpty(cc)) {
                messageHelper.setCc(cc.split(","));
            }
            //密送
            if (!StringUtils.isEmpty(bcc)) {
                messageHelper.setCc(bcc.split(","));
            }
            //添加邮件附件
            if (CollectionUtils.isNotEmpty(files)) {
                for (File file : files) {
                    messageHelper.addAttachment(file.getName(), file);
                }
            }
            // 邮件发送时间
            messageHelper.setSentDate(new Date());
            //正式发送邮件
            javaMailSender.send(messageHelper.getMimeMessage());
        } catch (Exception e) {
            throw new RuntimeException(ResultCodes.MAIL_SEND_ERROR.getMsg());
        }
    }


    public void sendText(String form, String to, String subject, String content) {
        this.send(title, form, to, subject, content, false, null, null, null);
    }


    public void sendHtml(String form, String to, String subject, String content) {;
        this.send(title, form, to, subject, content, true, null, null, null);
    }
}
