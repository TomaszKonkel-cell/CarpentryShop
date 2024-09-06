package com.example.carpentry.Repository.OrderRepository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.carpentry.Model.Order.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    public List<Cart> findAllByDateAndIsPaid(Date date, boolean isPaid);
}
