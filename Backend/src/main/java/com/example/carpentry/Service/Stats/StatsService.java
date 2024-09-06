package com.example.carpentry.Service.Stats;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;

public interface StatsService {

    public double todayEarnings();

    public ResponseEntity<?> earningsOfRange(long days, LocalDate startDate, LocalDate endDate);

    public int sumOfProjects();

    public ResponseEntity<?> sumOfProjectsRange(long days);

    public double percentageCompare();
}
