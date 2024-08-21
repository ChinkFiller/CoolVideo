package com.test.http_spring.service.impl;

import com.test.http_spring.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailSendImpl implements MailService {

    @Autowired
    JavaMailSender javaMailSender;

    @Override
    @Async
    public void sendMail(SimpleMailMessage mailMessage) {
        javaMailSender.send(mailMessage);
    }
}
