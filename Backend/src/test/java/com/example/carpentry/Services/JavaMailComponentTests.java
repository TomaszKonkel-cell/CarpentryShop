package com.example.carpentry.Services;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;

import com.example.carpentry.Service.MailSender.JavaMailComponent;
import com.example.carpentry.Service.Stats.StatsServiceImpl;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JavaMailComponentTests {

    @Mock
    JavaMailSender emailSender;

    @Mock
    StatsServiceImpl statsService;

    @InjectMocks
    JavaMailComponent javaMailComponent;

    @Test
    @Order(1)
    public void checkParam_whenToIsEmpty_thenReturnResponseIsBadRequestWithCustomMessage() {
        ResponseEntity<?> respone = javaMailComponent.checkParam("", "mail", LocalDate.now(), LocalDate.now());

        assertThat(respone.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(respone.getBody()).isEqualTo("Wymagany parametr odbiorcy");
    }

    @Test
    @Order(2)
    public void checkParam_whenSubjectIsEmpty_thenReturnResponseIsBadRequestWithCustomMessage() {
        ResponseEntity<?> respone = javaMailComponent.checkParam("t.konkel90@gmail.com", "", LocalDate.now(), LocalDate.now());

        assertThat(respone.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(respone.getBody()).isEqualTo("Wymagany parametr tematu");
    }

    @Test
    @Order(3)
    public void checkParam_whenStartDateIsNull_thenReturnResponseIsBadRequestWithCustomMessage() {
        ResponseEntity<?> respone = javaMailComponent.checkParam("t.konkel90@gmail.com", "Utarg", null, LocalDate.now());

        assertThat(respone.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(respone.getBody()).isEqualTo("Wymagany parametr daty początkowej");
    }

    @Test
    @Order(4)
    public void checkParam_whenEndDateIsNull_thenReturnResponseIsBadRequestWithCustomMessage() {
        ResponseEntity<?> respone = javaMailComponent.checkParam("t.konkel90@gmail.com", "Utarg", LocalDate.now(), null);

        assertThat(respone.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(respone.getBody()).isEqualTo("Wymagany parametr daty końcowej");
    }

}
