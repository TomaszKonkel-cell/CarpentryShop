package com.example.carpentry.Service.Storage;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.example.carpentry.Model.Storage;

public interface StorageService {

    public ResponseEntity<?> addItemStorage(Storage storage);

    public ResponseEntity<?> updateItemStorage(Long id, Storage storage);

    public void changeItemStorageQuantity(Storage storage, int quantity);

    public List<Storage> getItemsFromStorage();

    public Optional<Storage> findItemById(Long id);

    public Optional<Storage> findItem(String itemName);

    public ResponseEntity<?> deleteItem(Long id);

    public boolean checkIfExist (Storage storage);

}
