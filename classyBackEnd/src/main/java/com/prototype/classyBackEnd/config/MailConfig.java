package com.prototype.classyBackEnd.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

//이메일 인증을 위한 설정 클래스
@Log4j2
@Component
@Configuration
public class MailConfig {

    public MailConfig() throws IOException{
        log.info("MailConfig.java constructor called");
    }

    @Bean
    public JavaMailSender mailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties mailProperties = new Properties();
        mailProperties.put("mail.transport.protocol","smtp");
        mailProperties.put("mail.smtp.auth", "true");
        mailProperties.put("mail.smtp.starttls.enable", "true");
        mailProperties.put("mail.smtp.debug", "true");

        mailSender.setJavaMailProperties(mailProperties);
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("holli307@gmail.com");
        mailSender.setPassword("Alsrb0314**");
        mailSender.setDefaultEncoding("utf-8");
        return mailSender;
    }
}
