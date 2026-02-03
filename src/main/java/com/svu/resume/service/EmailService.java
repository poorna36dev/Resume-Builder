package com.svu.resume.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService  {
    
    @org.springframework.beans.factory.annotation.Value("${spring.mail.properties.mail.smtp.starttls.from}")
    private String fromEmail;
    private final JavaMailSender mailSender;
    public void sendHTMLMail(String to,String subject,String htmlContent) throws MessagingException{
        log.info("inside email service sendHtmlEmail()");
        MimeMessage message=mailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(message,true,"UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent,true);
        mailSender.send(message);
    }
}
