package com.example.carpentry.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.carpentry.Model.Order.Resources;
import com.example.carpentry.Service.Order.OrderServiceImpl;
import com.example.carpentry.Service.Order.ProjectsListServiceImpl;

@RestController
@RequestMapping("api/resources")
@CrossOrigin
public class ProjectsListController {

    @Autowired
    ProjectsListServiceImpl projectsListService;

    @Autowired
    OrderServiceImpl orderService;

    @GetMapping("details")
    public ResponseEntity<?> getProjectListDetails(@RequestParam Long id) {
        return new ResponseEntity<>(projectsListService.getProjectsList(id), HttpStatus.OK);
    }

    @PostMapping("add")
    public ResponseEntity<?> addResources(@RequestBody List<Resources> items, @RequestParam Long id) {
            return projectsListService.addResource(id, items);
    }

    @DeleteMapping("deleteResources")
    public ResponseEntity<?> deleteResources(@RequestParam Long projectsListId) {
        return projectsListService.deleteResources(projectsListId);
    }

}
