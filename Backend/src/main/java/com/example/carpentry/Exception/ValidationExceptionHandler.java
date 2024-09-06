package com.example.carpentry.Exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ValidationExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> notValidModelsData(MethodArgumentNotValidException ex, HttpServletRequest request) {
    List<String> errors = new ArrayList<>();

    ex.getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));

    Map<String, List<String>> result = new HashMap<>();
    result.put("errors", errors);

    return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> notValidType(HttpMessageNotReadableException ex, HttpServletRequest request) {

    return new ResponseEntity<>("Nie właściwy type danych :" + ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<?> missingParameterToRequest(MissingServletRequestParameterException ex,
      HttpServletRequest request) {

    return new ResponseEntity<>("Brakuje parametrów :" + ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<?> missingParameterToRequest(MissingServletRequestPartException ex,
      HttpServletRequest request) {

    return new ResponseEntity<>("Brakuje wymaganych parametrów do zapytania: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<?> notFoundProject(NotFoundException ex,
      HttpServletRequest request) {

    return new ResponseEntity<>("Nie odnaleziono danego projektu ", HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> wrongData(IllegalArgumentException ex,
      HttpServletRequest request) {

    return new ResponseEntity<>("Brak żądnych zasobów " + ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  
  
}