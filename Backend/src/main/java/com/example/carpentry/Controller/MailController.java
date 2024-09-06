package com.example.carpentry.Controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.carpentry.Service.MailSender.JavaMailComponent;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("api/mail")
@CrossOrigin
public class MailController {

    @Autowired
    JavaMailComponent javaMailComponent;

    @GetMapping("sendMail")
    public ResponseEntity<?> sendMail(@RequestParam String to, String subject, LocalDate start, LocalDate end)
            throws MessagingException {
        return javaMailComponent.sendSimpleMessage(to, subject, start, end);
    }

}
