package com.example.carpentry.Model;

import java.io.Serializable;

import com.example.carpentry.Enum.StorageItemCategories;
import com.example.carpentry.Enum.StorageItemType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Storage")
public class Storage implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nazwa jest wymagana")
    @Size(min = 3, message = "Nazwa musi mieć conajmniej 3 znaki")
    private String itemName;

    @NotNull(message = "Ilość jest wymagana")
    @PositiveOrZero(message = "Ilość musi być dodatnia")
    private int quantity;

    @NotBlank(message = "Opis jest wymagany")
    @Size(min = 3, message = "Opis musi mieć conajmniej 3 znaki")
    private String description;
    
    @Column(nullable = true)
    private String itemCode;

    @NotNull(message = "Typ przedmiotu jest wymagany")
    private StorageItemType type;

    @NotNull(message = "Kategoria przedmiotu jesy wymagana")
    private StorageItemCategories categories;

    public Storage() {
    }

    public Storage(Long id,
            String itemName,
            int quantity,
            String description,
            String itemCode,
            StorageItemType type,
            StorageItemCategories categories) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.description = description;
        this.itemCode = itemCode;
        this.type = type;
        this.categories = categories;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public StorageItemType getType() {
        return type;
    }

    public void setType(StorageItemType type) {
        this.type = type;
    }

    public StorageItemCategories getCategories() {
        return categories;
    }

    public void setCategories(StorageItemCategories categories) {
        this.categories = categories;
    }

}
