package com.example.carpentry.Service.MailSender;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import jakarta.mail.MessagingException;

@Configuration
@EnableScheduling
public class ScheduledMail {
    @Autowired
    private JavaMailComponent emailSender;

    @Scheduled(cron = "0 0 0 1 1/1 *")
    public void sendMail() throws MessagingException {
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();
        
        YearMonth date = YearMonth.of(year, month);
        LocalDate firstDay = date.atDay(1);
        LocalDate lastDay = date.plusMonths(1).atDay(1);

        emailSender.sendSimpleMessage("t.konkel90@gmail.com", "Miesięczny utarg", firstDay, lastDay);
        System.out.println("Podsumowanie miesiąca zostało wysłane");
    }

}
