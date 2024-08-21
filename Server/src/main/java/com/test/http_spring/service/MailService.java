package com.test.http_spring.service;

import org.springframework.mail.SimpleMailMessage;

public interface MailService {
    void sendMail(SimpleMailMessage mailMessage);
}
