package com.example.carpentry.Services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.security.GeneralSecurityException;
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
import com.example.carpentry.Model.Storage;
import com.example.carpentry.Model.Order.Resources;
import com.example.carpentry.Repository.StorageRepository;
import com.example.carpentry.Service.Order.ProjectsListServiceImpl;
import com.example.carpentry.Service.Storage.StorageServiceImpl;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StorageServiceTests {

    @Mock
    private StorageRepository storageRepository;

    @Mock
    ProjectsListServiceImpl projectsListService;

    @InjectMocks
    StorageServiceImpl storageService;

    private Storage storage;


    @BeforeEach
    public void setup() {
        storage = new Storage(1L, "Deska", 4, "To jest deska", null, StorageItemType.CONSTANT, StorageItemCategories.WOOD);
    }

    @Test
    @Order(1)
    public void createItem_withGoodData_thenCreateItem() {

        ResponseEntity<?> response = storageService.addItemStorage(storage);

        assertThat(response.getStatusCode().toString()).isEqualTo("201 CREATED");
        assertThat(response.getBody()).isEqualTo("Przedmiot dodany pomyślnie");
    }

    @Test
    @Order(2)
    public void createItem_withExistName_thenResponseIsBadRequestWithCustomMessage() {
        given(storageRepository.findByItemName(storage.getItemName())).willReturn(Optional.of(storage));

        ResponseEntity<?> response = storageService.addItemStorage(storage);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Istnieje przedmiot o tej samej nazwie");
    }

    @Test
    @Order(3)
    public void generateItemCode() {

        String code = storageService.generateItemCode(storage);

        assertThat(code).startsWith(storage.getType().toString().substring(0, 1));
        assertThat(code).endsWith(storage.getCategories().toString().substring(0, 1));
    }

    @Test
    @Order(4)
    public void updateItem_withGoodData_thenUpdateItem() {
        Storage newStorage = new Storage(1L, "NewDeska", 4, "To jest deska", null, StorageItemType.CONSTANT, StorageItemCategories.WOOD);
        given(storageRepository.findById(storage.getId())).willReturn(Optional.of(storage));
    
        ResponseEntity<?> response = storageService.updateItemStorage(storage.getId(), newStorage);

        assertThat(storage.getItemName()).isEqualTo("NewDeska");
        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");
        assertThat(response.getBody()).isEqualTo("Przedmiot zaktualizowany :");
    }

    @Test
    @Order(5)
    public void updateItem_withExistName_thenResponseIsBadRequestWithCustomMessage() {
        Storage existStorage = new Storage(2L, "DeskaNew", 4, "To jest deska", null, StorageItemType.CONSTANT, StorageItemCategories.WOOD);
        storage.setItemName("DeskaNew");
        given(storageRepository.findById(storage.getId())).willReturn(Optional.of(storage));
        given(storageRepository.findByItemName(storage.getItemName())).willReturn(Optional.of(existStorage));

        ResponseEntity<?> response = storageService.updateItemStorage(storage.getId(), storage);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Podana nowa nazwa jest zajęta");
    }

    @Test
    @Order(6)
    public void updateItem_withNotExistItem_thenResponseIsBadRequestWithCustomMessage() {

        ResponseEntity<?> response = storageService.updateItemStorage(storage.getId(), storage);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Podany przedmiot nie istnieje");
    }

    @Test
    @Order(7)
    public void deleteItem_withExistingInResources_thenResponseIsOKWithCustomMessage()
            throws GeneralSecurityException, IOException {

        given(storageRepository.findById(storage.getId())).willReturn(Optional.of(storage));
        given(projectsListService.getResourcesByStorage(storage)).willReturn(List.of(new Resources()));

        ResponseEntity<?> response = storageService.deleteItem(storage.getId());

        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");
        assertThat(response.getBody()).isEqualTo("Przedmiot należy do jakiegoś zasobu" + System.lineSeparator()
                        + "Ilość została ustawiona na zero");
    }

    @Test
    @Order(8)
    public void deleteItem_withNotExistingInResources_thenResponseIsOKWithCustomMessage()
            throws GeneralSecurityException, IOException {

        given(storageRepository.findById(storage.getId())).willReturn(Optional.of(storage));

        ResponseEntity<?> response = storageService.deleteItem(storage.getId());

        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");
        assertThat(response.getBody()).isEqualTo("Przedmiot usunięty");
    }

    @Test
    @Order(9)
    public void deleteItem_withNotExistingItem_thenResponseIsOKWithCustomMessage()
            throws GeneralSecurityException, IOException {


        ResponseEntity<?> response = storageService.deleteItem(storage.getId());

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Błąd!!! Podane zasoby do usunięcia nie istnieją");
    }
    
}
