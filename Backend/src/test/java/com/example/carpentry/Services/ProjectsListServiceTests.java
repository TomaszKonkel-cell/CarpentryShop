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
import com.example.carpentry.Repository.OrderRepository.ProjectsListRepository;
import com.example.carpentry.Repository.OrderRepository.ResourcesRepository;
import com.example.carpentry.Service.Order.OrderServiceImpl;
import com.example.carpentry.Service.Order.ProjectsListServiceImpl;
import com.example.carpentry.Service.Storage.StorageServiceImpl;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectsListServiceTests {

    @Mock
    ProjectsListRepository projectsListRepository;

    @Mock
    ResourcesRepository resourcesRepository;

    @Mock
    StorageServiceImpl storageService;

    @Mock
    OrderServiceImpl orderService;

    @InjectMocks
    ProjectsListServiceImpl projectsListService;

    List<Resources> listOfResources = new ArrayList<>();

    Project firstProject = new Project(1L, "Krzesło", 140, null, null, false);
    Project secondProject = new Project(2L, "KrzesłoV2", 160, null, null, false);

    ProjectsList firstProjectsList = new ProjectsList(4, firstProject, null);
    ProjectsList secondProjectsList = new ProjectsList(2, secondProject, null);

    Storage firstItem = new Storage(1L, "Deska", 4, "To jest deska", null, StorageItemType.CONSTANT,
            StorageItemCategories.WOOD);
    Storage secondItem = new Storage(2L, "Gwóźdź", 4, "To jest Gwóźdź", null, StorageItemType.CONSTANT,
            StorageItemCategories.METAL);

    Resources firstResource = new Resources(2, firstItem, firstProjectsList);
    Resources secondResource = new Resources(2, secondItem, secondProjectsList);

    Cart cart = new Cart(new Date(System.currentTimeMillis()), true, false, 1000.00);

    @BeforeEach
    public void setup() {
        listOfResources.add(firstResource);
        listOfResources.add(secondResource);

        firstProjectsList.setCart(cart);
        secondProjectsList.setCart(cart);
    }

    @Test
    @Order(1)
    public void addResource_withGoodData_thenAddResources() {
        given(projectsListRepository.findById(firstProjectsList.getId())).willReturn(Optional.of(firstProjectsList));
        given(storageService.checkIfExist(any(Storage.class))).willReturn(true);
        given(storageService.findItemById(firstResource.getItem().getId())).willReturn(Optional.of(firstItem));
        given(storageService.findItemById(secondResource.getItem().getId())).willReturn(Optional.of(secondItem));

        ResponseEntity<?> response = projectsListService.addResource(firstProjectsList.getId(), listOfResources);

        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");
        assertThat(response.getBody()).isEqualTo("Dodano zasoby");

    }

    @Test
    @Order(2)
    public void addResource_withNotExistingProjectsList_thenResponseIsBadRequestWithCustomMessage() {

        ResponseEntity<?> response = projectsListService.addResource(firstProjectsList.getId(), listOfResources);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Pozycja z zamówienia nie istnieje");

    }

    @Test
    @Order(2)
    public void addResource_withCompletedProjectsList_thenResponseIsBadRequestWithCustomMessage() {
        firstProjectsList.setResources(listOfResources);
        given(projectsListRepository.findById(firstProjectsList.getId())).willReturn(Optional.of(firstProjectsList));

        ResponseEntity<?> response = projectsListService.addResource(firstProjectsList.getId(), listOfResources);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Pozycja jest już uzupełniona");

    }

    @Test
    @Order(3)
    public void addResource_withNotPaidCartForProjectsList_thenResponseIsBadRequestWithCustomMessage() {
        cart.setPaid(false);
        given(projectsListRepository.findById(firstProjectsList.getId())).willReturn(Optional.of(firstProjectsList));

        ResponseEntity<?> response = projectsListService.addResource(firstProjectsList.getId(), listOfResources);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody())
                .isEqualTo("Nie można zapisać zasobów do bazy danych, bo zamówienie nie jest opłacone");

    }

    @Test
    @Order(4)
    public void addResource_withEmptyListOfResources_thenResponseIsBadRequestWithCustomMessage() {
        listOfResources.removeAll(listOfResources);
        given(projectsListRepository.findById(firstProjectsList.getId())).willReturn(Optional.of(firstProjectsList));

        ResponseEntity<?> response = projectsListService.addResource(firstProjectsList.getId(), listOfResources);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Brak przedmiotów w liście");

    }

    @Test
    @Order(5)
    public void addResource_withToHighQuantityForResource_thenResponseIsBadRequestWithCustomMessage() {
        firstResource.setQuantity(5);
        given(projectsListRepository.findById(firstProjectsList.getId())).willReturn(Optional.of(firstProjectsList));

        ResponseEntity<?> response = projectsListService.addResource(firstProjectsList.getId(), listOfResources);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Nie wystarczająca ilość przedmiotu: Deska");

    }

    @Test
    @Order(6)
    public void addResource_withNotExistingStorage_thenResponseIsBadRequestWithCustomMessage() {
        given(projectsListRepository.findById(firstProjectsList.getId())).willReturn(Optional.of(firstProjectsList));
        given(storageService.checkIfExist(any(Storage.class))).willReturn(false);

        ResponseEntity<?> response = projectsListService.addResource(firstProjectsList.getId(), listOfResources);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Podany przedmiot nie jest obecny");

    }

    @Test
    @Order(7)
    public void addResource_withNotMatchingQuantity_thenResponseIsBadRequestWithCustomMessage() {
        firstResource.setQuantity(-2);
        given(projectsListRepository.findById(firstProjectsList.getId())).willReturn(Optional.of(firstProjectsList));
        given(storageService.checkIfExist(any(Storage.class))).willReturn(true);

        ResponseEntity<?> response = projectsListService.addResource(firstProjectsList.getId(), listOfResources);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Parametry liczbowe muszą być obecne i dodatnie");

    }

    @Test
    @Order(8)
    public void addResource_withRepeatedStorageInResourcesList_thenResponseIsBadRequestWithCustomMessage() {
        listOfResources.removeAll(listOfResources);
        listOfResources.add(firstResource);
        listOfResources.add(firstResource);
        given(projectsListRepository.findById(firstProjectsList.getId())).willReturn(Optional.of(firstProjectsList));
        given(storageService.checkIfExist(any(Storage.class))).willReturn(true);

        ResponseEntity<?> response = projectsListService.addResource(firstProjectsList.getId(), listOfResources);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("W żądaniu znajdują się powtórzone przedmioty");

    }

    @Test
    @Order(9)
    public void changeQunatity_withGoodData_thenChangeQuantity() {
        int quantityToSub = 1;
        int originalQuantity = firstItem.getQuantity();
        given(storageService.findItemById(firstResource.getItem().getId())).willReturn(Optional.of(firstItem));

        projectsListService.changeQuantity(firstItem.getId(), quantityToSub);

        assertThat(firstItem.getQuantity()).isEqualTo(originalQuantity - quantityToSub);

    }

    @Test
    @Order(10)
    public void changeQuantity_withNotFoundStorage_thenThrowExceptionWithCustomMessage() {

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> {
                    projectsListService.changeQuantity(firstItem.getId(), 1);
                });

        assertThat(ex.getMessage()).isEqualTo("Nie znaleziono przedmiotu");

    }

    @Test
    @Order(11)
    public void changeQuantity_withToHighQuantity_thenThrowExceptionWithCustomMessage() {
        given(storageService.findItemById(firstResource.getItem().getId())).willReturn(Optional.of(firstItem));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> {
                    projectsListService.changeQuantity(firstItem.getId(), 5);
                });

        assertThat(ex.getMessage()).isEqualTo("Nie wystarczająca ilość przedmiotu: " + firstItem.getItemName());

    }

    @Test
    @Order(12)
    public void deleteResources_withGoodData_thenDeleteResource() {
        firstProjectsList.setResources(listOfResources);
        given(projectsListRepository.findById(firstProjectsList.getId())).willReturn(Optional.of(firstProjectsList));

        ResponseEntity<?> response = projectsListService.deleteResources(firstProjectsList.getId());

        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");
        assertThat(response.getBody()).isEqualTo("Zasoby usunięte z bazy, a zlecenie przywrócono");

    }

    @Test
    @Order(13)
    public void deleteResources_withNotFoundProjectsList_thenThrowExceptionWithCustomMessage() {

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> {
                    projectsListService.deleteResources(firstItem.getId());
                });

        assertThat(ex.getMessage()).isEqualTo("Nie znaleziono pozycji");

    }

}
