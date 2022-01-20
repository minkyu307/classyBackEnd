package com.prototype.classyBackEnd.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.mail.MessagingException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.xml.bind.ValidationException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<Map<String, String>> ServerExceptionHandler(Exception e) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("ErrorCode", e.getMessage());
        return ResponseEntity.internalServerError().body(map);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<Map<String, String>> ValidationExceptionHandler(ValidationException e) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("ErrorCode", e.getMessage());
        return ResponseEntity.badRequest().body(map);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({MessagingException.class, UnsupportedEncodingException.class})
    public ResponseEntity<Map<String, String>> JavaMailSendExceptionHandler(Exception e){
        Map<String, String> map = new LinkedHashMap<>();
        map.put("ErrorCode", "EmailNotSent");
        return ResponseEntity.internalServerError().body(map);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({NoResultException.class})
    public ResponseEntity<Map<String, String>> getSingleResultExceptionHandler1(Exception e){
        Map<String, String> map = new LinkedHashMap<>();
        map.put("ErrorCode", "SqlNoResult");
        return ResponseEntity.internalServerError().body(map);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({NonUniqueResultException.class})
    public ResponseEntity<Map<String, String>> getSingleResultExceptionHandler2(Exception e){
        Map<String, String> map = new LinkedHashMap<>();
        map.put("ErrorCode", "SqlNonUniqueResult");
        return ResponseEntity.internalServerError().body(map);
    }
}
