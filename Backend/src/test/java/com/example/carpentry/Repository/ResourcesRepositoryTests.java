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

import com.example.carpentry.Enum.StorageItemCategories;
import com.example.carpentry.Enum.StorageItemType;
import com.example.carpentry.Model.Project;
import com.example.carpentry.Model.Storage;
import com.example.carpentry.Model.Order.Cart;
import com.example.carpentry.Model.Order.ProjectsList;
import com.example.carpentry.Model.Order.Resources;
import com.example.carpentry.Repository.OrderRepository.CartRepository;
import com.example.carpentry.Repository.OrderRepository.ProjectsListRepository;
import com.example.carpentry.Repository.OrderRepository.ResourcesRepository;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ResourcesRepositoryTests {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    ProjectsListRepository projectsListRepository;

    @Autowired
    ResourcesRepository resourcesRepository;

    private Storage item;
    private ProjectsList projectsList;
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

        if (storageRepository.count() == 0) {
            item = new Storage(1L,
                    "Deska",
                    15,
                    "To jest deska",
                    null,
                    StorageItemType.CONSTANT,
                    StorageItemCategories.WOOD);

            storageRepository.save(item);
        }

        if (projectsListRepository.count() == 0) {
            projectsList = new ProjectsList(2, project, cart);

            projectsListRepository.save(projectsList);
        }

    }

    @Test
    @Order(1)
    @Rollback(value = false)
    public void createResourcesTest() {
        Resources resource = new Resources(2, item, projectsList);

        resourcesRepository.save(resource);

        assertThat(resource.getId()).isGreaterThan(0);

    }

    @Test
    @Order(2)
    public void getResourcesByIdTest() {
        Resources findResources = resourcesRepository.findById(1L).get();

        assertThat(findResources.getId()).isEqualTo(1L);
    }

    @Test
    @Order(3)
    public void getListOfResourcesTest() {
        List<Resources> resourcesList = resourcesRepository.findAll();

        assertThat(resourcesList.size()).isGreaterThan(0);

    }

    @Test
    @Order(4)
    @Rollback(value = false)
    public void getListOfResourcesByItemTest() {
        Optional<Storage> findItem = storageRepository.findById(1L);
        List<Resources> resourcesListByItem = resourcesRepository.findAllByItem(findItem.get());

        assertThat(resourcesListByItem).isNotEmpty();
    }

    @Test
    @Order(5)
    @Rollback(value = false)
    public void updateResourcesTest() {
        Resources findResourcesById = resourcesRepository.findById(1L).get();
        findResourcesById.setQuantity(3);
        Resources resourcesUpdated = resourcesRepository.save(findResourcesById);

        assertThat(resourcesUpdated.getQuantity()).isEqualTo(3);

    }

    @Test
    @Order(6)
    @Rollback(value = false)
    public void deleteResourcesTest() {
        resourcesRepository.deleteById(1L);
        Optional<Resources> resourcesOptional = resourcesRepository.findById(1L);

        assertThat(resourcesOptional).isEmpty();
    }

}
