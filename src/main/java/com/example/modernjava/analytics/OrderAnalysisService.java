package com.example.modernjava.analytics;

import com.example.modernjava.domain.Address;
import com.example.modernjava.domain.Customer;
import com.example.modernjava.domain.Order;
import com.example.modernjava.domain.OrderItem;
import com.example.modernjava.domain.Product;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class OrderAnalysisService {
    private final List<Order> orders;

    public OrderAnalysisService(@NonNull List<Order> orders) {
        this.orders = orders;
    }

    /**
     * Get a list of distinct countries related to the orders by getting
     * postal country code from the various addresses associated with the orders
     * <p>
     * Illustrates the use of distinct, flatMap, Stream.of and Stream.ofNullable
     * as a means of safely dealing with stream elements that may be null.
     * <p>
     * .flatMap(Stream::ofNullable) could easily be implemented using filter
     * as an alternative (e.g. .filter(a -> a != null))
     *
     * @return a list of distinct country codes
     */
    public List<String> distinctCountries() {
        return orders.stream().flatMap(order -> Stream.of(order.getAltShippingAddress(),
                        order.getCustomer().getBillingAddress(),
                        order.getCustomer().getShippingAddress())).flatMap(Stream::ofNullable).map(Address::getPostalCountry)
                .distinct().toList();
    }

    /**
     * Get a list of distinct customers related to all orders
     * <p>
     * Illustrates the use of map, distinct and toList.
     *
     * @return a list of distinct customers related to all orders
     */
    public List<Customer> distinctCustomers() {
        return orders.stream().map(Order::getCustomer).distinct().toList();
    }

    /**
     * Get the total number of orders is the order collections used to
     * initialize this class
     *
     * @return total number of orders
     */
    public long totalOrders() {
        return orders.size();
    }

    /**
     * Get the total number of units sold for all orders
     * <p>
     * Illustrates flatMapToInt, mapToInt, reduce
     *
     * @return total number of units sold
     */
    public long totalUnitsSold() {
        //return orders.stream().flatMapToInt(order -> order.getItems().stream().mapToInt(OrderItem::getQuantity)).sum();
        return orders.stream()
                        .flatMapToInt(order -> order.getItems().stream()
                                .mapToInt(OrderItem::getQuantity))
                .reduce(Integer::sum).orElse(0); // Could use Collectors.summingInt as alternative
    }

    /**
     * Get the total number of units sold for all orders, grouped by product
     * <p>
     * Illustrates flatMap, groupingBy, and sunningInt
     *
     * @return total number of units sold, grouped by product
     */
    public Map<String, Integer> totalUnitsSoldByProduct() {
        return orders.stream().
                flatMap(order -> order.getItems().stream())
                .collect(groupingBy(item -> item.getProduct().getName(),
                    Collectors.summingInt(OrderItem::getQuantity))); // Could use reduce(Integer::sum) as alternative
    }

    /**
     * Get the total number of units sold for all orders, grouped by customer and product
     * <p>
     * Illustrates groupingBy, flatMapping and summingInt. This is a good example of
     * grouping using elements from multiple levels of an object hierarchy.
     *
     * @return total number of units sold, grouped by customer and product
     */
    public Map<String, Map<String, Integer>> totalUnitsSoldByCustomerByProduct() {
        return orders.stream().collect(groupingBy(order -> order.getCustomer().getName(),
                flatMapping(order -> order.getItems().stream(),
                        groupingBy(item -> item.getProduct().getName(),
                                Collectors.summingInt(OrderItem::getQuantity)))));
    }

    /**
     * Get the total number of units sold for all orders, grouped by country and product
     * <p>
     * Illustrates groupingBy, flatMapping and summingInt. This is a good example of
     * grouping using elements from multiple levels of an object hierarchy.
     *
     * @return total number of units sold, grouped by country and product
     */
    public Map<String, Map<String, Integer>> totalUnitsSoldByCountryByProduct() {
        return orders.stream().collect(groupingBy(order -> order.getCustomer().getBillingAddress().getPostalCountry(),
                flatMapping(order -> order.getItems().stream(),
                        groupingBy(item -> item.getProduct().getName(),
                                Collectors.summingInt(OrderItem::getQuantity)))));
    }

    // TODO: Implement the following functions and their accompanying tests using streams

    // totalRevenue
    /**
     * Get the total revenue for all orders in the order collections used to
     * initialize this class
     * <p>
     * Revenue per order item == Quantity * Price * (1 - Discount)
     *
     * @return total revenue for all orders
     */
    public BigDecimal totalRevenue() {
        return BigDecimal.valueOf(orders.stream()
                .flatMapToDouble(order -> order.getItems().stream()
                        .mapToDouble(item -> BigDecimal.valueOf(item.getQuantity())
                                .multiply(item.getProduct().getPrice())
                                .multiply(BigDecimal.valueOf(1).subtract(item.getDiscount()))
                                .doubleValue()))
                .sum());
    }

    // totalRevenueByProduct
    /**
     * Get the total revenue for each product in the order collections used to
     * initialize this class
     * <p>
     * Revenue per order item == Quantity * Price * (1 - Discount)
     * product represented by name
     * @return total revenue for each product
     */
    public Map<String,BigDecimal> totalRevenueByProduct() {

        /* trying to use the following structure to calculate revenue instead of quantity sum:
        Map<String, Integer> result = orders.stream()
            .flatMap(order -> order.getItems().stream())
            .collect(groupingBy(item -> item.getProduct().getName(),
                Collectors.reducing(0, OrderItem::getQuantity, Integer::sum)
            ));
         */
        Map<Object,Object> result = orders.stream()
                .flatMap(order -> order.getItems().stream())
                .collect(groupingBy(item -> item.getProduct().getName(),
                                Collectors.reducing(BigDecimal.ZERO, OrderItem::getProduct,
                                        (item1, item2) -> {
//                                            BigDecimal a = BigDecimal.valueOf(item1.getQuantity())
//                                                .multiply(item1.getProduct().getPrice())
//                                                .multiply(BigDecimal.valueOf(1).subtract(item1.getDiscount()));
//                                            BigDecimal b = BigDecimal.valueOf(item2.getQuantity())
//                                                    .multiply(item2.getProduct().getPrice())
//                                                    .multiply(BigDecimal.valueOf(1).subtract(item2.getDiscount()));
//                                            return a.multiply(b); // revenue accum for items 1 and 2
                                            return item2;
                                })
                ));


        System.out.println("result: >> " +result);
        Map<String,BigDecimal> hm = new HashMap<>();
        hm.put("sample", BigDecimal.valueOf(15.69));
        return hm;
    }



    // totalRevenueByCustomer
    // totalRevenueByCustomerByProduct
    // totalRevenueByCountry
    // totalRevenueByCountryByProduct
    // BONUS: product stats (hint: see IntStream::summaryStatistics())
    // BONUS: revenue stats
}
