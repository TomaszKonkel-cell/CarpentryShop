package com.example.carpentry.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.carpentry.Model.Storage;
import com.example.carpentry.Service.Storage.StorageServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/storage")
@CrossOrigin
public class StorageController {

    @Autowired
    StorageServiceImpl storageService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        return new ResponseEntity<>(storageService.getItemsFromStorage(), HttpStatus.OK);
    }

    @GetMapping("/details")
    public ResponseEntity<?> details(@RequestParam Long id) {
            return new ResponseEntity<>(storageService.findItemById(id), HttpStatus.OK);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> add(@Valid @ModelAttribute Storage storage) {
        return storageService.addItemStorage(storage);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> update(@RequestParam Long id, @Valid @ModelAttribute Storage storage) {
        return storageService.updateItemStorage(id, storage);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> delete(@RequestParam Long id) {
        return storageService.deleteItem(id);
    }
}
