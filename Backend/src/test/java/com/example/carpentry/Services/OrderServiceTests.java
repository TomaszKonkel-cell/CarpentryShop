package com.example.carpentry.Services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.example.carpentry.Enum.StorageItemCategories;
import com.example.carpentry.Enum.StorageItemType;
import com.example.carpentry.Model.Project;
import com.example.carpentry.Model.Storage;
import com.example.carpentry.Model.Order.Cart;
import com.example.carpentry.Model.Order.ProjectsList;
import com.example.carpentry.Model.Order.Resources;
import com.example.carpentry.Repository.OrderRepository.CartRepository;
import com.example.carpentry.Repository.OrderRepository.ProjectsListRepository;
import com.example.carpentry.Service.Order.OrderServiceImpl;
import com.example.carpentry.Service.Order.ProjectsListServiceImpl;
import com.example.carpentry.Service.Projects.ProjectsServiceImpl;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderServiceTests {

    @Mock
    ProjectsListRepository projectsListRepository;

    @Mock
    CartRepository cartRepository;

    @Mock
    ProjectsServiceImpl projectsService;

    @Mock
    ProjectsListServiceImpl projectsListService;

    @InjectMocks
    OrderServiceImpl orderService;

    List<ProjectsList> listOfProject = new ArrayList<>();

    Project firstProject = new Project(1L, "Krzesło", 140, null, null, false);
    Project secondProject = new Project(2L, "KrzesłoV2", 160, null, null, false);

    Storage storage = new Storage(1L, "Deska", 4, "To jest deska", null, StorageItemType.CONSTANT, StorageItemCategories.WOOD);

    ProjectsList firstProjectsList = new ProjectsList(4, firstProject, null);
    ProjectsList secondProjectsList = new ProjectsList(2, secondProject, null);

    Cart cart = new Cart(new Date(System.currentTimeMillis()), true, false, 1000.00);

    Resources resource = new Resources(2, storage, firstProjectsList);

    @BeforeEach
    public void setup() {
        firstProjectsList.setId(1L);
        secondProjectsList.setId(2L);
        listOfProject.add(firstProjectsList);
        listOfProject.add(secondProjectsList);
    }

    @Test
    @Order(1)
    public void createCart_withGoodData_thenCreateCart() {
        given(projectsService.checkIfExist(any(Project.class))).willReturn(true);
        given(projectsService.getProject(firstProject.getId())).willReturn(Optional.of(firstProject));
        given(projectsService.getProject(secondProject.getId())).willReturn(Optional.of(secondProject));

        ResponseEntity<?> response = orderService.createCart(listOfProject, false);

        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");
        assertThat(response.getBody()).isEqualTo("Przyjęto zamówienie");
    }

    @Test
    @Order(2)
    public void createCart_withEmptyList_thenResponseIsBadRequestWithCustomMessage() {
        List<ProjectsList> emptyListOfProject = new ArrayList<>();
        ResponseEntity<?> response = orderService.createCart(emptyListOfProject, false);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Brak pozycji w zamówieniu");
    }

    @Test
    @Order(3)
    public void createCart_withNotExistingProjectInList_thenResponseIsBadRequestWithCustomMessage() {
        given(projectsService.checkIfExist(any(Project.class))).willReturn(false);
    
        ResponseEntity<?> response = orderService.createCart(listOfProject, false);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Podany projekt nie jest obecny");
    }

    @Test
    @Order(4)
    public void createCart_withNotMatchingPrice_thenResponseIsBadRequestWithCustomMessage() {
        given(projectsService.checkIfExist(any(Project.class))).willReturn(true);
        given(projectsService.getProject(firstProject.getId())).willReturn(Optional.of(firstProject));
        given(projectsService.getProject(secondProject.getId())).willReturn(Optional.of(firstProject));
    
        ResponseEntity<?> response = orderService.createCart(listOfProject, false);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Wystąpił błąd!!! Nie zgadzają się parametry ceny dla: Krzesło");
    }

    @Test
    @Order(5)
    public void createCart_withNotValidQuantity_thenResponseIsBadRequestWithCustomMessage() {
        ProjectsList wrongProjectsList = new ProjectsList(-2, secondProject, null);
        listOfProject.removeAll(listOfProject);
        listOfProject.add(firstProjectsList);
        listOfProject.add(wrongProjectsList);

        given(projectsService.checkIfExist(any(Project.class))).willReturn(true);
        given(projectsService.getProject(firstProject.getId())).willReturn(Optional.of(firstProject));
        given(projectsService.getProject(secondProject.getId())).willReturn(Optional.of(secondProject));
    
        ResponseEntity<?> response = orderService.createCart(listOfProject, false);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Parametry liczbowe muszą być obecne i dodatnie");
    }

    @Test
    @Order(6)
    public void createCart_withRepeatedProject_thenResponseIsBadRequestWithCustomMessage() {
        listOfProject.removeAll(listOfProject);
        listOfProject.add(firstProjectsList);
        listOfProject.add(firstProjectsList);

        given(projectsService.checkIfExist(any(Project.class))).willReturn(true);
        given(projectsService.getProject(firstProject.getId())).willReturn(Optional.of(firstProject));
    
        ResponseEntity<?> response = orderService.createCart(listOfProject, false);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("W żądaniu znajdują się powtórzone projekty");
    }

    @Test
    @Order(7)
    public void checkIfCloseOrder_withOneProjectsListInCart_thenReturnTrue() {
        firstProjectsList.setCart(cart);
        cart.setProjects(List.of(firstProjectsList));
        given(projectsListService.getProjectsList(firstProjectsList.getId())).willReturn(Optional.of(firstProjectsList));
    
        boolean result = orderService.checkIfCloseOrder(firstProjectsList.getId());

        assertThat(result).isEqualTo(true);
    }

    @Test
    @Order(8)
    public void checkIfCloseOrder_withMultipleProjectsListInCartWithoutResources_thenReturnTrue() {
        firstProjectsList.setCart(cart);
        cart.setProjects(listOfProject);
        given(projectsListService.getProjectsList(firstProjectsList.getId())).willReturn(Optional.of(firstProjectsList));
    
        boolean result = orderService.checkIfCloseOrder(firstProjectsList.getId());
        
        assertThat(result).isEqualTo(false);
    }

    @Test
    @Order(9)
    public void checkIfCloseOrder_withMultipleProjectsListInCartWithResources_thenReturnTrue() {
        firstProjectsList.setCart(cart);
        cart.setProjects(listOfProject);
        firstProjectsList.setResources(List.of(resource));
        secondProjectsList.setResources(List.of(resource));
        given(projectsListService.getProjectsList(firstProjectsList.getId())).willReturn(Optional.of(firstProjectsList));
    
        boolean result = orderService.checkIfCloseOrder(firstProjectsList.getId());
        
        assertThat(result).isEqualTo(true);
    }

    @Test
    @Order(10)
    public void checkIfCloseOrder_withMultipleProjectsListInCartWithNotAllResources_thenReturnTrue() {
        firstProjectsList.setCart(cart);
        cart.setProjects(listOfProject);
        firstProjectsList.setResources(List.of(resource));
        given(projectsListService.getProjectsList(firstProjectsList.getId())).willReturn(Optional.of(firstProjectsList));
    
        boolean result = orderService.checkIfCloseOrder(firstProjectsList.getId());
        
        assertThat(result).isEqualTo(false);
    }

    @Test
    @Order(11)
    public void checkIfCloseOrder_withNotFoundProjectsList_thenThrowExceptionWithCustomMessage() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> {
                    orderService.checkIfCloseOrder(firstProjectsList.getId());
                });

        assertThat(ex.getMessage()).isEqualTo("Nie znaleziono pozycji");
    }

    @Test
    @Order(12)
    public void changeOrderStatus_withGoodData_thenChangeStatus() {
        cart.setDone(true);
        given(cartRepository.findById(cart.getId())).willReturn(Optional.of(cart));
    
        orderService.changeOrderStatus(cart.getId());
        
        assertThat(cart.isDone()).isEqualTo(false);
    }

    @Test
    @Order(13)
    public void changeOrderStatus_withNotFoundCart_thenThrowExceptionWithCustomMessage() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> {
                    orderService.changeOrderStatus(cart.getId());
                });

        assertThat(ex.getMessage()).isEqualTo("Nie znaleziono zamówienia");
    }

    @Test
    @Order(14)
    public void changePaidStatus_withGoodData_thenChangeStatus() {
        given(cartRepository.findById(cart.getId())).willReturn(Optional.of(cart));
    
        orderService.changePaidStatus(cart.getId());
        
        assertThat(cart.isPaid()).isEqualTo(true);
    }

    @Test
    @Order(15)
    public void changePaidStatus_withNotFoundCart_thenThrowExceptionWithCustomMessage() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> {
                    orderService.changePaidStatus(cart.getId());
                });

        assertThat(ex.getMessage()).isEqualTo("Nie znaleziono zamówienia");
    }

    @Test
    @Order(16)
    public void changePaidStatus_withGoodData_thenChangeStatusAndResponseIsOk() {
        given(cartRepository.findById(cart.getId())).willReturn(Optional.of(cart));
    
        ResponseEntity<?> response = orderService.changePaidStatus(cart.getId());

        assertThat(cart.isPaid()).isTrue();
        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");
        assertThat(response.getBody()).isEqualTo("Zamówienie opłacone");
    }
}
