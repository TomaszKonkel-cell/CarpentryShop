package com.example.carpentry.Service.Projects;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.example.carpentry.Model.Project;

public interface ProjectsService {
    public Optional<Project> getProject(Long id);

    public List<Project> getProjects();

    public Optional<Project> findProject(String name);

    public boolean checkIfExist(Project project);

    public ResponseEntity<?> createProject(Project project, MultipartFile file) throws GeneralSecurityException;

    public ResponseEntity<?> updateProject(Long id, Project project, MultipartFile file) throws IOException, GeneralSecurityException;

    public ResponseEntity<?> deleteProject(Long id);

    public ResponseEntity<?> restoreProject(Long id);

}
