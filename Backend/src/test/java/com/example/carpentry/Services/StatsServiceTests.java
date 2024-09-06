package com.example.carpentry.Services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.example.carpentry.Model.Order.Cart;
import com.example.carpentry.Model.Order.ProjectsList;
import com.example.carpentry.Repository.OrderRepository.CartRepository;
import com.example.carpentry.Service.Order.OrderServiceImpl;
import com.example.carpentry.Service.Stats.StatsServiceImpl;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StatsServiceTests {

    @Mock
    CartRepository cartRepository;

    @Mock
    OrderServiceImpl orderService;

    @InjectMocks
    StatsServiceImpl statsService;

    Cart todayCart = new Cart(new Date(System.currentTimeMillis()), true, false, 2200.00);
    Cart firstCart = new Cart(Date.valueOf(LocalDate.now().minusDays(3)), true, false, 1000.00);
    Cart secondCart = new Cart(Date.valueOf(LocalDate.now().minusDays(2)), true, false, 1200.00);

    ProjectsList firstProjectsList = new ProjectsList(4, null, null);
    ProjectsList secondProjectsList = new ProjectsList(2, null, null);

    List<Cart> listOfCart = List.of(todayCart, firstCart, secondCart);

    @BeforeEach
    public void setup() {
        firstCart.setProjects(List.of(firstProjectsList));
        secondCart.setProjects(List.of(secondProjectsList));

    }

    @Test
    @Order(1)
    public void getTodayEarnings_thenReturnSum() {
        given(orderService.getAll()).willReturn(listOfCart);

        double sum = statsService.todayEarnings();

        assertThat(sum).isEqualTo(2200.00);
    }

    @Test
    @Order(2)
    public void getTodayEarnings_whenListOfCartIsEmpty_thenReturnSum() {
        double sum = statsService.todayEarnings();

        assertThat(sum).isEqualTo(0);
    }

    @Test
    @Order(3)
    public void describeListOfDates_WithDaysParameter_thenReturnListOfDates() {
        long days = 3;
        List<LocalDate> listOfDates = statsService.describeListOfDates(days, null, null);

        assertThat(listOfDates)
                .isEqualTo(LocalDate.now().minusDays(3).datesUntil(LocalDate.now()).collect(Collectors.toList()));
    }

    @Test
    @Order(4)
    public void describeListOfDates_WithoutDaysParameter_thenReturnListOfDates() {
        LocalDate startDate = LocalDate.of(2024, 9, 1);
        LocalDate endDate = LocalDate.of(2024, 9, 4);
        List<LocalDate> listOfDates = statsService.describeListOfDates(0, startDate, endDate);

        assertThat(listOfDates).isEqualTo(startDate.datesUntil(endDate).collect(Collectors.toList()));
    }

    @Test
    @Order(5)
    public void describeListOfEarnings_WithListOfDates_thenReturnListOfDates() {
        List<LocalDate> listOfDates = statsService.describeListOfDates(3, null, null);
        TreeMap<LocalDate, Double> sortedMapOfEarnings = new TreeMap<>();
        sortedMapOfEarnings.put(LocalDate.now().minusDays(3), 1000.00);
        sortedMapOfEarnings.put(LocalDate.now().minusDays(2), 1200.00);
        sortedMapOfEarnings.put(LocalDate.now().minusDays(1), 0.00);
        given(orderService.getAllByDate(Date.valueOf(LocalDate.now().minusDays(3)))).willReturn(List.of(firstCart));
        given(orderService.getAllByDate(Date.valueOf(LocalDate.now().minusDays(2)))).willReturn(List.of(secondCart));

        Map<LocalDate, Double> listOfEarnings = statsService.describeListOfEarnings(listOfDates);

        assertThat(listOfEarnings).isEqualTo(sortedMapOfEarnings);
    }

    @Test
    @Order(6)
    public void earningsOfRange_WithListOfDates_thenReturnListOfDates() {
        TreeMap<LocalDate, Double> sortedMapOfEarnings = new TreeMap<>();
        sortedMapOfEarnings.put(LocalDate.now().minusDays(3), 1000.00);
        sortedMapOfEarnings.put(LocalDate.now().minusDays(2), 1200.00);
        sortedMapOfEarnings.put(LocalDate.now().minusDays(1), 0.00);
        given(orderService.getAllByDate(Date.valueOf(LocalDate.now().minusDays(3)))).willReturn(List.of(firstCart));
        given(orderService.getAllByDate(Date.valueOf(LocalDate.now().minusDays(2)))).willReturn(List.of(secondCart));

        ResponseEntity<?> response = statsService.earningsOfRange(3, null, null);

        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");
        assertThat(response.getBody()).isEqualTo(sortedMapOfEarnings);
    }

    @Test
    @Order(7)
    public void sumeEarningsOfRange_WithListOfDates_thenReturnListOfDates() {
        given(orderService.getAllByDate(Date.valueOf(LocalDate.now().minusDays(3)))).willReturn(List.of(firstCart));
        given(orderService.getAllByDate(Date.valueOf(LocalDate.now().minusDays(2)))).willReturn(List.of(secondCart));

        String result = statsService.sumEarningsOfRange(3, null, null);

        assertThat(result).isEqualTo("2200.0");
    }

    @Test
    @Order(8)
    public void sumOfProjects_thenReturnSum() {
        firstCart.setDone(true);
        firstCart.setDate(new Date(System.currentTimeMillis()));
        secondCart.setDone(true);
        secondCart.setDate(new Date(System.currentTimeMillis()));
        given(orderService.getAll()).willReturn(listOfCart);

        int sum = statsService.sumOfProjects();

        assertThat(sum).isEqualTo(2);
    }

    @Test
    @Order(9)
    public void describeListOfSum_thenReturnListOfSum() {
        LocalDate xDaysAgo = LocalDate.now().minusDays(3);
        LocalDate today = LocalDate.now();
        TreeMap<LocalDate, Integer> sortedMapOfSum = new TreeMap<>();
        sortedMapOfSum.put(LocalDate.now().minusDays(3), 1);
        sortedMapOfSum.put(LocalDate.now().minusDays(2), 1);
        sortedMapOfSum.put(LocalDate.now().minusDays(1), 0);
        given(orderService.getAllByDate(Date.valueOf(LocalDate.now().minusDays(3)))).willReturn(List.of(firstCart));
        given(orderService.getAllByDate(Date.valueOf(LocalDate.now().minusDays(2)))).willReturn(List.of(secondCart));

        Map<LocalDate, Integer> result = statsService.describeListOfSum(xDaysAgo, today);

        assertThat(result).isEqualTo(sortedMapOfSum);
    }

    @Test
    @Order(10)
    public void sumOfProjectsRange_thenReturnListOfSumAndResponseIsOK() {
        TreeMap<LocalDate, Integer> sortedMapOfSum = new TreeMap<>();
        sortedMapOfSum.put(LocalDate.now().minusDays(3), 1);
        sortedMapOfSum.put(LocalDate.now().minusDays(2), 1);
        sortedMapOfSum.put(LocalDate.now().minusDays(1), 0);
        given(orderService.getAllByDate(Date.valueOf(LocalDate.now().minusDays(3)))).willReturn(List.of(firstCart));
        given(orderService.getAllByDate(Date.valueOf(LocalDate.now().minusDays(2)))).willReturn(List.of(secondCart));

        ResponseEntity<?> response = statsService.sumOfProjectsRange(3L);

        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");
        assertThat(response.getBody()).isEqualTo(sortedMapOfSum);
    }

    @Test
    @Order(11)
    public void percentageCompare_whenExistCartForTodayAndYesterDay_thenReturnPercentageDiff() {
        TreeMap<LocalDate, Integer> sortedMapOfSum = new TreeMap<>();
        sortedMapOfSum.put(LocalDate.now().minusDays(3), 1);
        sortedMapOfSum.put(LocalDate.now().minusDays(2), 1);
        sortedMapOfSum.put(LocalDate.now().minusDays(1), 0);
        given(orderService.getAllByDate(Date.valueOf(LocalDate.now()))).willReturn(List.of(firstCart));
        given(orderService.getAllByDate(Date.valueOf(LocalDate.now().minusDays(1)))).willReturn(List.of(secondCart));

        double result = statsService.percentageCompare();

        assertThat(result).isEqualTo(-16.666666666666668);
    }

    @Test
    @Order(12)
    public void percentageCompare_whenNotExistYesterdayCart_thenReturnPercentageDiff() {
        TreeMap<LocalDate, Integer> sortedMapOfSum = new TreeMap<>();
        sortedMapOfSum.put(LocalDate.now().minusDays(3), 1);
        sortedMapOfSum.put(LocalDate.now().minusDays(2), 1);
        sortedMapOfSum.put(LocalDate.now().minusDays(1), 0);
        given(orderService.getAllByDate(Date.valueOf(LocalDate.now()))).willReturn(List.of(firstCart));

        double result = statsService.percentageCompare();

        assertThat(result).isEqualTo(firstCart.getTotalPrice());

    }

    
}
