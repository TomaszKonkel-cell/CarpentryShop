package com.example.carpentry.Repository.OrderRepository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.carpentry.Model.Project;
import com.example.carpentry.Model.Order.ProjectsList;

@Repository
public interface ProjectsListRepository extends JpaRepository<ProjectsList, Long>{
    
    public List<ProjectsList> findAllByProject(Project project);
}
