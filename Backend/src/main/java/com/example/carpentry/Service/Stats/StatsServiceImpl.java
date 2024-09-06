package com.example.carpentry.Service.Stats;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.carpentry.Model.Order.Cart;
import com.example.carpentry.Repository.OrderRepository.CartRepository;
import com.example.carpentry.Service.Order.OrderServiceImpl;

@Service
public class StatsServiceImpl implements StatsService {
    @Autowired
    CartRepository cartRepository;

    @Autowired
    OrderServiceImpl orderService;

    @Override
    public double todayEarnings() {
        List<Cart> orders = orderService.getAll();

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat pattern = new SimpleDateFormat("yyyy-MM-dd");
        String today = pattern.format(date);
        double sum = orders.stream()
                .filter(item -> pattern.format(item.getDate()).equals(today) && item.isPaid() == true)
                .mapToDouble(item -> item.getTotalPrice()).sum();

        return sum;

    }

    @Override
    public ResponseEntity<?> earningsOfRange(long days, LocalDate startDate, LocalDate endDate) {

        List<LocalDate> listOfDates = describeListOfDates(days, startDate, endDate);
        Map<LocalDate, Double> lastXDaysEarningsList = describeListOfEarnings(listOfDates);

        return new ResponseEntity<>(lastXDaysEarningsList, HttpStatus.OK);
    }

    public List<LocalDate> describeListOfDates(long days, LocalDate startDate, LocalDate endDate) {
        if (days != 0) {
            LocalDate xDaysAgo = LocalDate.now().minusDays(days);
            LocalDate today = LocalDate.now();
            List<LocalDate> listOfDates = xDaysAgo.datesUntil(today).collect(Collectors.toList());
            return listOfDates;
        }

        if (days == 0) {
            List<LocalDate> listOfDates = startDate.datesUntil(endDate).collect(Collectors.toList());
            return listOfDates;
        }

        return null;

    }

    public Map<LocalDate, Double> describeListOfEarnings(List<LocalDate> listOfDates) {
        Map<LocalDate, Double> mapOfEarnings = new HashMap<>();

        listOfDates.forEach(date -> {
            Date converDate = Date.valueOf(date);
            List<Cart> orderByDate = orderService.getAllByDate(converDate);

            if (orderByDate.size() == 0) {
                mapOfEarnings.put(date, Double.valueOf(0));

            }
            if (orderByDate.size() == 1) {
                mapOfEarnings.put(date, Double.valueOf(orderByDate.get(0).getTotalPrice()));

            } else if (orderByDate.size() > 1) {
                double sum = orderByDate.stream()
                        .mapToDouble(item -> item.getTotalPrice()).sum();
                mapOfEarnings.put(date, Double.valueOf(sum));
            }

        });
        TreeMap<LocalDate, Double> sortedMapOfEarnings = new TreeMap<>(mapOfEarnings);
        return sortedMapOfEarnings;

    }

    public String sumEarningsOfRange(long days, LocalDate starDate, LocalDate endDate) {

        List<LocalDate> listOfDates = describeListOfDates(days, starDate, endDate);
        List<Double> values = new ArrayList<Double>();
        listOfDates.forEach(date -> {
            Date converDate = Date.valueOf(date);
            List<Cart> orderByDate = orderService.getAllByDate(converDate);

            values.add(orderByDate.stream()
                    .mapToDouble(item -> item.getTotalPrice()).sum());

        });
        Double sum = values.stream()
                .mapToDouble(item -> item).sum();
        return sum.toString();
    }

    @Override
    public int sumOfProjects() {
        List<Cart> orders = orderService.getAll();

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat pattern = new SimpleDateFormat("yyyy-MM-dd");
        String today = pattern.format(date);
        int sum = orders.stream()
                .filter(item -> pattern.format(item.getDate()).equals(today) && item.isDone() == true)
                .mapToInt(item -> item.getProjects().size()).sum();

        return sum;

    }

    @Override
    public ResponseEntity<?> sumOfProjectsRange(long days) {
        LocalDate xDaysAgo = LocalDate.now().minusDays(days);
        LocalDate today = LocalDate.now();

        Map<LocalDate, Integer> lastXDaysSumOfProjects = describeListOfSum(xDaysAgo, today);

        return new ResponseEntity<>(lastXDaysSumOfProjects, HttpStatus.OK);
    }

    public Map<LocalDate, Integer> describeListOfSum(LocalDate xDaysAgo, LocalDate today) {
        Map<LocalDate, Integer> mapOfSum = new HashMap<>();
        List<LocalDate> listOfDates = xDaysAgo.datesUntil(today).collect(Collectors.toList());

        listOfDates.forEach(date -> {
            Date converDate = Date.valueOf(date);
            List<Cart> orderByDate = orderService.getAllByDate(converDate);

            if (orderByDate.size() == 0) {
                mapOfSum.put(date, Integer.valueOf(0));
            }
            if (orderByDate.size() == 1) {
                mapOfSum.put(date, Integer.valueOf(orderByDate.get(0).getProjects().size()));
            } else if (orderByDate.size() > 1) {
                int sum = orderByDate.stream()
                        .mapToInt(item -> item.getProjects().size()).sum();
                mapOfSum.put(date, Integer.valueOf(sum));
            }

        });

        TreeMap<LocalDate, Integer> sortedMapOfSum = new TreeMap<>(mapOfSum);
        return sortedMapOfSum;

    }

    @Override
    public double percentageCompare() {
        double percentageDiff = 0.0;
        LocalDate today = LocalDate.now();
        LocalDate yesterday = LocalDate.now().minusDays(1);

        List<Cart> todayOrders = orderService.getAllByDate(Date.valueOf(today));
        List<Cart> yesterdayOrders = orderService.getAllByDate(Date.valueOf(yesterday));

        double sumOfTodayOrders = todayOrders.stream()
                .mapToDouble(item -> item.getTotalPrice()).sum();

        double sumOfYesterdayOrders = yesterdayOrders.stream()
                .mapToDouble(item -> item.getTotalPrice()).sum();

        if (sumOfYesterdayOrders > 0 ) {
            percentageDiff = 100.00 * ((sumOfTodayOrders - sumOfYesterdayOrders)) / Math.abs(sumOfYesterdayOrders);
        }
        if (sumOfYesterdayOrders == 0 ) {
            percentageDiff = sumOfTodayOrders;
        }

        return percentageDiff;
    }

}
