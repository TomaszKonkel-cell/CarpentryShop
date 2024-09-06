package com.example.carpentry.Service.Order;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.carpentry.Model.Order.Cart;
import com.example.carpentry.Model.Order.ProjectsList;
import com.example.carpentry.Repository.OrderRepository.CartRepository;
import com.example.carpentry.Repository.OrderRepository.ProjectsListRepository;
import com.example.carpentry.Service.Projects.ProjectsServiceImpl;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    ProjectsListRepository projectsListRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProjectsServiceImpl projectsService;

    @Autowired
    ProjectsListServiceImpl projectsListService;

    @Override
    public List<Cart> getAll() {
        return cartRepository.findAll();
    }

    @Override
    public Optional<Cart> getOrderById(Long id) {
        return cartRepository.findById(id);
    }

    @Override
    public List<Cart> getAllByDate(Date date) {
        return cartRepository.findAllByDateAndIsPaid(date, true);
    }

    @Override
    public ResponseEntity<?> createCart(List<ProjectsList> projectsList, boolean isPaid) {
        if (checkData(projectsList).getStatusCode().is2xxSuccessful()) {
            double totalPrice = projectsList.stream()
                    .mapToDouble(item -> item.getProject().getPrice() * item.getQuantity()).sum();

            Cart cart = new Cart(new Date(System.currentTimeMillis()), isPaid, false, totalPrice);
            cartRepository.save(cart);

            projectsList.forEach(i -> {
                ProjectsList item = new ProjectsList(i.getQuantity(), i.getProject(), cart);
                projectsListRepository.save(item);
            });

            cart.setProjects(projectsList);
            return new ResponseEntity<>("Przyjęto zamówienie", HttpStatus.OK);
        } else {
            return checkData(projectsList);
        }

    }

    @Override
    public ResponseEntity<?> checkData(List<ProjectsList> projectsList) {
        List<Long> ids = new ArrayList<>();
        if (projectsList.size() == 0) {
            return new ResponseEntity<>("Brak pozycji w zamówieniu", HttpStatus.BAD_REQUEST);
        } else {
            for (ProjectsList i : projectsList) {
                ids.add(i.getProject().getId());
                if (!projectsService.checkIfExist(i.getProject())) {
                    return new ResponseEntity<>("Podany projekt nie jest obecny", HttpStatus.BAD_REQUEST);
                }

                double correctPrice = projectsService.getProject(i.getProject().getId()).get().getPrice();
                double requestPrice = i.getProject().getPrice();

                if (correctPrice != requestPrice) {
                    String projectName = projectsService.getProject(i.getProject().getId()).get().getName();
                    return new ResponseEntity<>("Wystąpił błąd!!! Nie zgadzają się parametry ceny dla: " + projectName,
                            HttpStatus.BAD_REQUEST);
                }

                if (i.getQuantity() <= 0 || i.getProject().getPrice() < 0) {
                    return new ResponseEntity<>("Parametry liczbowe muszą być obecne i dodatnie",
                            HttpStatus.BAD_REQUEST);
                }
            }
        }
        List<Long> checkDuplicates = ids.stream().distinct().collect(Collectors.toList());

        if (ids.size() != checkDuplicates.size()) {
            return new ResponseEntity<>("W żądaniu znajdują się powtórzone projekty", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public boolean checkIfCloseOrder(Long projectListId) {
        boolean[] result = { false };
        ProjectsList projectsList = projectsListService.getProjectsList(projectListId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono pozycji"));
        Cart checkingCart = projectsList.getCart();
        List<ProjectsList> checkingCartProjectsList = checkingCart.getProjects();

        System.out.println(checkingCart);
        System.out.println(checkingCartProjectsList);

        if (checkingCartProjectsList.size() == 1) {
            result[0] = true;
        } else {
            checkingCartProjectsList.forEach(item -> {
                if (item.getId() != projectListId) {
                    if (item.getResources().size() > 0) {
                        result[0] = true;
                    } else {
                        result[0] = false;
                    }
                }

            });
        }

        if (result[0] == true) {
            checkingCart.setDone(true);
            cartRepository.save(checkingCart);
        }
        return result[0];
    }

    @Override
    public void changeOrderStatus(Long orderId) {
        Cart cart = cartRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono zamówienia"));

        if (cart.isDone() == true) {
            cart.setDone(false);
            cartRepository.save(cart);
        }
    }

    @Override
    public ResponseEntity<String> changePaidStatus(Long orderId) {
        Cart cart = cartRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono zamówienia"));

        if (cart.isPaid() == false) {
            cart.setPaid(true);
            cartRepository.save(cart);
        }
        return new ResponseEntity<>("Zamówienie opłacone", HttpStatus.OK);
    }

}
