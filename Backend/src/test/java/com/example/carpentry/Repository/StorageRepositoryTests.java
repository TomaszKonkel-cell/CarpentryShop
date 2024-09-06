package com.example.carpentry.Repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.example.carpentry.Enum.StorageItemCategories;
import com.example.carpentry.Enum.StorageItemType;
import com.example.carpentry.Model.Storage;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StorageRepositoryTests {

    @Autowired
    private StorageRepository storageRepository;

    @Test
    @Order(1)
    @Rollback(value = false)
    public void createItemTest() {
        Storage item = new Storage(1L,
                "Deska",
                15,
                "To jest deska",
                null,
                StorageItemType.CONSTANT,
                StorageItemCategories.WOOD);

        storageRepository.save(item);

        assertThat(item.getId()).isGreaterThan(0);

    }

    @Test
    @Order(2)
    public void getItemByIdTest() {
        Storage findItem = storageRepository.findById(1L).get();

        assertThat(findItem.getId()).isEqualTo(1L);
    }

    @Test
    @Order(3)
    public void getItemByNameTest() {
        Storage findItem = storageRepository.findByItemName("Deska").get();

        assertThat(findItem.getItemName()).isEqualTo("Deska");
    }

    @Test
    @Order(4)
    public void getItemByItemCodeTest() {
        Collection<Storage> findItem = storageRepository.findByItemCode(null);

        assertThat(findItem).isNotEmpty();
    }

    @Test
    @Order(5)
    public void getListOfItemsTest() {
        List<Storage> itemList = storageRepository.findAll();

        assertThat(itemList.size()).isGreaterThan(0);

    }

    @Test
    @Order(6)
    @Rollback(value = false)
    public void updateItemTest() {
        Storage findItemById = storageRepository.findById(1L).get();
        findItemById.setItemName("Listwa");
        Storage itemUpdated = storageRepository.save(findItemById);

        assertThat(itemUpdated.getItemName()).isEqualTo("Listwa");

    }

    @Test
    @Order(7)
    @Rollback(value = false)
    public void checkIfExist() {
        boolean itemFirst = storageRepository.existsById(1L);
        boolean itemSecond = storageRepository.existsById(8L);

        assertThat(itemFirst).isEqualTo(true);
        assertThat(itemSecond).isEqualTo(false);
    }

    @Test
    @Order(8)
    @Rollback(value = false)
    public void deleteItemTest() {
        storageRepository.deleteById(1L);
        Optional<Storage> itemOptional = storageRepository.findById(1L);

        assertThat(itemOptional).isEmpty();
    }

}
