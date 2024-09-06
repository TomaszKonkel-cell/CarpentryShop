package com.example.carpentry.Repository;

import java.util.Optional;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.carpentry.Model.Storage;

@Repository
public interface StorageRepository extends JpaRepository <Storage, Long> {

    Optional<Storage> findByItemName(String itemName);

    Collection<Storage> findByItemCode(String itemCode);

}
