package com.example.carpentry.Service.Order;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.example.carpentry.Model.Order.Cart;
import com.example.carpentry.Model.Order.ProjectsList;

public interface OrderService {

    public ResponseEntity<?> createCart(List<ProjectsList> projectsList, boolean isPaid);

    public void changeOrderStatus(Long orderId);

    public ResponseEntity<?> changePaidStatus(Long orderId);

    public boolean checkIfCloseOrder(Long projectListId);

    public List<Cart> getAll();

    public Optional<Cart> getOrderById(Long id);

    public List<Cart> getAllByDate(Date date);

    public ResponseEntity<?> checkData(List<ProjectsList> projectsList);
}
