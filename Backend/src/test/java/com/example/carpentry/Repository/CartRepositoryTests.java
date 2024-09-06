package com.example.carpentry.Repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.example.carpentry.Model.Order.Cart;
import com.example.carpentry.Repository.OrderRepository.CartRepository;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CartRepositoryTests {

    @Autowired
    private CartRepository cartRepository;

    @Test
    @Order(1)
    @Rollback(value = false)
    public void createCartTest() {
        Cart cart = new Cart(new Date(System.currentTimeMillis()), false, false, 640.00);

        cartRepository.save(cart);

        assertThat(cart.getId()).isGreaterThan(0);

    }

    @Test
    @Order(2)
    public void getCartByIdTest() {
        Cart findCart = cartRepository.findById(1L).get();

        assertThat(findCart.getId()).isEqualTo(1L);
    }

    @Test
    @Order(3)
    public void getListOfCartsTest() {
        List<Cart> cartList = cartRepository.findAll();

        assertThat(cartList.size()).isGreaterThan(0);

    }

    @Test
    @Order(4)
    @Rollback(value = false)
    public void updateCartTest() {
        Cart findCartById = cartRepository.findById(1L).get();
        findCartById.setPaid(true);
        Cart cartUpdated = cartRepository.save(findCartById);

        assertThat(cartUpdated.isPaid()).isEqualTo(true);

    }

    @Test
    @Order(5)
    public void getCartByDateAndPaidTest() {
        List<Cart> findCartByDateAndPaid = cartRepository.findAllByDateAndIsPaid(new Date(System.currentTimeMillis()), true);

        assertThat(findCartByDateAndPaid).isNotEmpty();
    }

    @Test
    @Order(6)
    @Rollback(value = false)
    public void deleteCartTest() {
        cartRepository.deleteById(1L);
        Optional<Cart> cartOptional = cartRepository.findById(1L);

        assertThat(cartOptional).isEmpty();
    }
}
