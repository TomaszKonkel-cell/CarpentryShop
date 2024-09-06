package com.example.carpentry.Service.Order;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.example.carpentry.Model.Project;
import com.example.carpentry.Model.Storage;
import com.example.carpentry.Model.Order.ProjectsList;
import com.example.carpentry.Model.Order.Resources;

public interface ProjectsListService {
    public Optional<ProjectsList> getProjectsList(Long id);
    
    public List<ProjectsList> getProjectsListByProject(Project project);

    public List<Resources> getResourcesByStorage(Storage item);

    public ResponseEntity<?>  addResource(Long id, List<Resources> resources);

    public ResponseEntity<?> checkData(Long id, List<Resources> resources);

    public ResponseEntity<?> deleteResources(Long projectsListId);

    

}
