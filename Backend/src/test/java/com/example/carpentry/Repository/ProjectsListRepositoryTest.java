package com.example.carpentry.Repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.example.carpentry.Model.Project;
import com.example.carpentry.Model.Order.Cart;
import com.example.carpentry.Model.Order.ProjectsList;
import com.example.carpentry.Repository.OrderRepository.CartRepository;
import com.example.carpentry.Repository.OrderRepository.ProjectsListRepository;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectsListRepositoryTest {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProjectsListRepository projectsListRepository;

    private Project project;
    private Cart cart;

    @BeforeEach
    public void setup() {

        if (projectRepository.count() == 0) {
            project = new Project(1L,
                    "Krzesło",
                    160.00,
                    "To jest opis",
                    null,
                    true);

            projectRepository.save(project);
        }

        if (cartRepository.count() == 0) {
            cart = new Cart(new Date(System.currentTimeMillis()), false, false, 640.00);

            cartRepository.save(cart);
        }

    }

    @Test
    @Order(1)
    @Rollback(value = false)
    public void createProjectsListTest() {
        ProjectsList projectsList = new ProjectsList(2, project, cart);

        projectsListRepository.save(projectsList);

        assertThat(cart.getId()).isGreaterThan(0);

    }

    @Test
    @Order(2)
    public void getProjectsListByIdTest() {
        ProjectsList findProjectsList = projectsListRepository.findById(1L).get();

        assertThat(findProjectsList.getId()).isEqualTo(1L);
    }

    @Test
    @Order(3)
    public void getListOfProjectsListTest() {
        List<ProjectsList> projectsListList = projectsListRepository.findAll();

        assertThat(projectsListList.size()).isGreaterThan(0);

    }

    @Test
    @Order(4)
    @Rollback(value = false)
    public void getListOfProjectsListByProjectTest() {
        Optional<Project> findProject = projectRepository.findById(1L);
        List<ProjectsList> projectsListListByProject = projectsListRepository.findAllByProject(findProject.get());

        assertThat(projectsListListByProject).isNotEmpty();
    }

    @Test
    @Order(5)
    @Rollback(value = false)
    public void updateProjectsListTest() {
        ProjectsList findProjectsListById = projectsListRepository.findById(1L).get();
        findProjectsListById.setQuantity(3);
        ProjectsList projectsListUpdated = projectsListRepository.save(findProjectsListById);

        assertThat(projectsListUpdated.getQuantity()).isEqualTo(3);

    }

    @Test
    @Order(6)
    @Rollback(value = false)
    public void deleteProjectsListTest() {
        projectsListRepository.deleteById(1L);
        Optional<ProjectsList> projectsListOptional = projectsListRepository.findById(1L);

        assertThat(projectsListOptional).isEmpty();
    }

}
