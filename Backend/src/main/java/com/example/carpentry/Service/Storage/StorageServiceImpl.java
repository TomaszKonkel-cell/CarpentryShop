package com.example.carpentry.Service.Storage;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.carpentry.Model.Storage;
import com.example.carpentry.Repository.StorageRepository;
import com.example.carpentry.Service.Order.ProjectsListServiceImpl;
import com.mifmif.common.regex.Generex;

@Service
public class StorageServiceImpl implements StorageService {

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    ProjectsListServiceImpl projectsListService;

    @Override
    public List<Storage> getItemsFromStorage() {
        return storageRepository.findAll();
    }

    @Override
    public Optional<Storage> findItemById(Long id) {
        return storageRepository.findById(id);
    }

    @Override
    public Optional<Storage> findItem(String itemName) {
        return storageRepository.findByItemName(itemName);
    } 
    
    @Override
    public boolean checkIfExist(Storage storage) {
        return storageRepository.existsById(storage.getId());
    }

    @Override
    public ResponseEntity<?> addItemStorage(Storage storage) {
        if (findItem(storage.getItemName()).isPresent()) {
            return new ResponseEntity<>("Istnieje przedmiot o tej samej nazwie", HttpStatus.BAD_REQUEST);
        }
        storage.setItemCode(generateItemCode(storage));
        storageRepository.save(storage);
        return new ResponseEntity<>("Przedmiot dodany pomyślnie", HttpStatus.CREATED);
    }

    public String generateItemCode(Storage storage) {
        String firstLetter = storage.getType().toString().substring(0, 1);
        String lastLetter = storage.getCategories().toString().substring(0, 1);
        String randomCode = null;

        Generex generex = new Generex("[0-9]([a-z]{4})");
        randomCode = firstLetter + generex.random() + lastLetter;
        if (storageRepository.count() > 0 && !storageRepository.findByItemCode(randomCode).isEmpty()) {
            generateItemCode(storage);
        }
        return randomCode;

    }

    @Override
    public ResponseEntity<?> updateItemStorage(Long id, Storage storage) {
        Optional<Storage> itemData = findItemById(id);
        Optional<Storage> checkName = findItem(storage.getItemName());
        if (checkName.isPresent() && checkName.get().getId() != id) {
            return new ResponseEntity<>("Podana nowa nazwa jest zajęta", HttpStatus.BAD_REQUEST);
        }
        if (itemData.isPresent()) {
            Storage newItem = itemData.get();
            newItem.setItemName(storage.getItemName());
            newItem.setQuantity(storage.getQuantity());
            newItem.setDescription(storage.getDescription());
            newItem.setItemCode(itemData.get().getItemCode());
            newItem.setType(storage.getType());
            newItem.setCategories(storage.getCategories());
            storageRepository.save(newItem);
            return new ResponseEntity<>("Przedmiot zaktualizowany :", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Podany przedmiot nie istnieje", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> deleteItem(Long id) {
        Optional<Storage> findStorageItem = findItemById(id);

        if (findStorageItem.isPresent()) {
            if (!projectsListService.getResourcesByStorage(findStorageItem.get()).isEmpty()) {
                findStorageItem.get().setQuantity(0);
                storageRepository.save(findStorageItem.get());
                return new ResponseEntity<>("Przedmiot należy do jakiegoś zasobu" + System.lineSeparator()
                        + "Ilość została ustawiona na zero", HttpStatus.OK);
            } else {
                storageRepository.deleteById(id);
                return new ResponseEntity<>("Przedmiot usunięty", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Błąd!!! Podane zasoby do usunięcia nie istnieją", HttpStatus.BAD_REQUEST);
    }

   
    @Override
    public void changeItemStorageQuantity(Storage storage, int quantity) {
        Storage item = findItemById(storage.getId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono pozycji"));
        item.setQuantity(item.getQuantity() + quantity);
        storageRepository.save(item);
    }

}
