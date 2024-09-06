package com.example.carpentry.Model.Order;

import com.example.carpentry.Model.Storage;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Resources")
public class Resources {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    int quantity;

    @ManyToOne
    @JoinColumn(name = "storage_id")
    private Storage item;

    @ManyToOne
    @JoinColumn(name = "projectsList_id")
    private ProjectsList projectsList;

    

    public Resources(int quantity, Storage item, ProjectsList projectsList) {
        this.quantity = quantity;
        this.item = item;
        this.projectsList = projectsList;
    }

    public Resources() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Storage getItem() {
        return item;
    }

    public void setItem(Storage item) {
        this.item = item;
    }

    @JsonIgnore
    public ProjectsList getProjectsList() {
        return projectsList;
    }

    public void setProjectsList(ProjectsList projectsList) {
        this.projectsList = projectsList;
    }

    

}
