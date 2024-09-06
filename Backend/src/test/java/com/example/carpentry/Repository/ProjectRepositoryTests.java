package com.example.carpentry.Repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.example.carpentry.Model.Project;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectRepositoryTests {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @Order(1)
    @Rollback(value = false)
    public void saveProjectTest() {
        Project project = new Project(1L,
        "Krzesło",
        160.00,
        "To jest opis",
        null,
        true);

        projectRepository.save(project);

        assertThat(project.getId()).isGreaterThan(0);
    }

    @Test
    @Order(2)
    public void getProjectByIdTest() {
        Project findProject = projectRepository.findById(1L).get();

        assertThat(findProject.getId()).isEqualTo(1L);
    }

    @Test
    @Order(3)
    public void getProjectByNameTest() {
        Project findProject = projectRepository.findByName("Krzesło").get();

        assertThat(findProject.getName()).isEqualTo("Krzesło");
    }

    @Test
    @Order(4)
    public void getListOfProjectsTest() {
        List<Project> projectList = projectRepository.findAll();

        assertThat(projectList.size()).isGreaterThan(0);

    }

    @Test
    @Order(5)
    @Rollback(value = false)
    public void updateProjectTest() {
        Project findProjectById = projectRepository.findById(1L).get();
        findProjectById.setName("Szafka");
        Project projectUpdated = projectRepository.save(findProjectById);

        assertThat(projectUpdated.getName()).isEqualTo("Szafka");

    }

    @Test
    @Order(6)
    @Rollback(value = false)
    public void deleteUserTest() {
        projectRepository.deleteById(1L);
        Optional<Project> projectOptional = projectRepository.findById(1L);

        assertThat(projectOptional).isEmpty();
    }

}
