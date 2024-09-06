package com.example.carpentry.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.carpentry.Model.Order.ProjectsList;
import com.example.carpentry.Service.Order.OrderServiceImpl;
import com.example.carpentry.Service.Payment.PaymentServiceImpl;
import com.stripe.exception.StripeException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/order")
@CrossOrigin
public class OrderController {

    @Autowired
    OrderServiceImpl orderService;

    @Autowired
    PaymentServiceImpl paymentService;

    @PostMapping("add")
    public ResponseEntity<?> createCart(@Valid @RequestBody List<ProjectsList> items, @RequestParam boolean isPaid) throws NotFoundException {
            return orderService.createCart(items, isPaid);
    }

    @GetMapping("all")
    public ResponseEntity<?> all() {
        return new ResponseEntity<>(orderService.getAll(), HttpStatus.OK);
    }

    @GetMapping("getOrderById")
    public ResponseEntity<?> getOrderById(@RequestParam Long orderId) {
        return new ResponseEntity<>(orderService.getOrderById(orderId), HttpStatus.OK);
    }

    @PutMapping("end")
    public boolean closeOrder(@RequestParam Long projectsListId) {
        return orderService.checkIfCloseOrder(projectsListId);
    }

    @PutMapping("changePaidStatus")
    public ResponseEntity<?> changePaidStatus(@RequestParam Long orderId) {
        return orderService.changePaidStatus(orderId);
    }

    @PostMapping("payment")
    public String createPayment(@RequestBody List<ProjectsList> items) throws NotFoundException, StripeException {
        String link = paymentService.createPaymentLink(items);
        return link;
    }

}
