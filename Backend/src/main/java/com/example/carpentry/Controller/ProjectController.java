package com.example.carpentry.Controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.carpentry.Model.Project;
import com.example.carpentry.Service.Image.ImageServiceImpl;
import com.example.carpentry.Service.Projects.ProjectsServiceImpl;
import com.google.gson.Gson;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin
public class ProjectController {

    Gson gson = new Gson();

    @Autowired
    ProjectsServiceImpl projectsService;

    @Autowired
    ImageServiceImpl imageService;

    @GetMapping("/all")
    public List<Project> getAll() {
        return projectsService.getProjects();
    }

    @GetMapping("/details")
    public Optional<Project> details(@RequestParam Long id) {
            return projectsService.getProject(id);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> add(@Valid @ModelAttribute Project project, MultipartFile file) throws GeneralSecurityException {
        return projectsService.createProject(project, file);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> update(@RequestParam Long id, @Valid @ModelAttribute Project project, MultipartFile file) throws GeneralSecurityException, IOException {
        return projectsService.updateProject(id, project, file);

    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> delete(@RequestParam Long id) {
        return projectsService.deleteProject(id);
    }

    @GetMapping("/restore")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> restore(@RequestParam Long id) {
        return projectsService.restoreProject(id);
    }
}
