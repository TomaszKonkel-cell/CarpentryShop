package com.example.carpentry.Service.MailSender;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.carpentry.Service.Stats.StatsServiceImpl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class JavaMailComponent {

  @Autowired
  private JavaMailSender emailSender;

  @Autowired
  private StatsServiceImpl statsService;

  public ResponseEntity<?> sendSimpleMessage(
      String to, String subject, LocalDate start, LocalDate end) throws MessagingException {

    if (checkParam(to, subject, start, end).getStatusCode().is2xxSuccessful()) {
      MimeMessage message = emailSender.createMimeMessage();

      message.setFrom("t.konkel90@gmail.com");
      message.setRecipients(MimeMessage.RecipientType.TO, to);
      message.setSubject(subject);

      String view = templateView(start, end);

      message.setContent(view, "text/html; charset=utf-8");

      emailSender.send(message);
      return new ResponseEntity<>("Wiadomość wysłana", HttpStatus.OK);
    } else {
      return checkParam(to, subject, start, end);
    }

  }

  public String templateView(LocalDate start, LocalDate end) {
    String listOfEarnings = statsService.earningsOfRange(0, start, end).getBody().toString();
    String sumOfEarnings = statsService.sumEarningsOfRange(0, start, end);
    String splitListOfEarnings[] = listOfEarnings.split(",");
    String earnings = "";

    for (String word : splitListOfEarnings) {
      earnings = earnings + word + "zł" + "<br>";
    }

    String head = "Podsumowanie na dni: " + "<strong>" + start + "</strong>" + " - " + "<strong>" + end + "</strong>"
        + "<br>";

    earnings = earnings.replaceAll("\\{", " ").replaceAll("\\}", " ").replaceAll("=", " = ").replaceAll("\\.0", "");

    String footer = "<br>" + "Suma sprzedaży wynosi: " + sumOfEarnings + "zł";

    String output = head + earnings + footer.replaceAll("\\.0", "");

    return output;
  }

  public ResponseEntity<?> checkParam(String to, String subject, LocalDate start, LocalDate end) {
    if (to.isEmpty())
      return new ResponseEntity<>("Wymagany parametr odbiorcy", HttpStatus.BAD_REQUEST);
    if (subject.isEmpty())
      return new ResponseEntity<>("Wymagany parametr tematu", HttpStatus.BAD_REQUEST);
    if (start == null)
      return new ResponseEntity<>("Wymagany parametr daty początkowej", HttpStatus.BAD_REQUEST);
    if (end == null)
      return new ResponseEntity<>("Wymagany parametr daty końcowej", HttpStatus.BAD_REQUEST);

    return new ResponseEntity<>(null, HttpStatus.OK);

  }

}
