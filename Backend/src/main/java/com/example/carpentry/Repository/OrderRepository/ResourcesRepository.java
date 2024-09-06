package com.example.carpentry.Repository.OrderRepository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.carpentry.Model.Storage;
import com.example.carpentry.Model.Order.Resources;

@Repository
public interface ResourcesRepository extends JpaRepository<Resources, Long>{

    public List<Resources> findAllByItem(Storage item);
}
