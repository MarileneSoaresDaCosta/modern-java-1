package com.example.modernjava.analytics;

import com.example.modernjava.domain.Address;
import com.example.modernjava.domain.Customer;
import com.example.modernjava.domain.Order;
import com.example.modernjava.domain.OrderItem;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.groupingBy;

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
        return orders.stream().flatMapToInt(order -> order.getItems().stream().mapToInt(OrderItem::getQuantity)).reduce(
                Integer::sum).orElse(0); // Could use Collectors.summingInt as alternative
    }

    /**
     * Get the total number of units sold for all orders, grouped by product
     * <p>
     * Illustrates flatMap, groupingBy, and sunningInt
     *
     * @return total number of units sold, grouped by product
     */
    public Map<String, Integer> totalUnitsSoldByProduct() {
        return orders.stream().flatMap(order -> order.getItems().stream()).collect(groupingBy(item -> item.getProduct()
                        .getName(),
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
    // totalRevenueByProduct
    // totalRevenueByCustomer
    // totalRevenueByCustomerByProduct
    // totalRevenueByCountry
    // totalRevenueByCountryByProduct
    // BONUS: product stats (hint: see IntStream::summaryStatistics())
    // BONUS: revenue stats
}
