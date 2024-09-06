package com.example.carpentry.Controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.carpentry.Service.Image.ImageServiceImpl;
import com.example.carpentry.Service.MailSender.JavaMailComponent;
import com.example.carpentry.Service.Projects.ProjectsServiceImpl;
import com.example.carpentry.Service.Stats.StatsServiceImpl;
import com.example.carpentry.Service.Storage.StorageServiceImpl;

@RestController
@RequestMapping("api/stats")
@CrossOrigin
public class StatsController {

    @Autowired
    StatsServiceImpl statsService;

    @Autowired
    ProjectsServiceImpl projectsService;

    @Autowired
    StorageServiceImpl storageService;

    @Autowired
    JavaMailComponent javaMailComponent;

    @Autowired
    ImageServiceImpl imageService;

    @GetMapping("todayEarnings")
    public double todayEarnings() {
        return statsService.todayEarnings();
    }

    @GetMapping("earningsOfRange")
    public ResponseEntity<?> earningsOfRange(@RequestParam long days, LocalDate startDate, LocalDate endDate) {
        return statsService.earningsOfRange(days, startDate, endDate);
    }

    @GetMapping("sumOfProjects")
    public int sumOfProjects() {
        return statsService.sumOfProjects();
    }

    @GetMapping("sumOfProjectsRange")
    public ResponseEntity<?> sumOfProjectsRange(@RequestParam long days) {
        return statsService.sumOfProjectsRange(days);
    }

    @GetMapping("percentageCompare")
    public double percentageCompare() {
        return statsService.percentageCompare();
    }
}
