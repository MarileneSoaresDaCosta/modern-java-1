package com.example.modernjava.repositories;

import com.example.modernjava.domain.Order;
import org.springframework.lang.NonNull;

import java.util.List;

public class OrderTestRepository {
    private final List<Order> orders;

    public OrderTestRepository(@NonNull List<Order> orders) {
        this.orders = List.copyOf(orders);
    }

    public Iterable<Order> findAll() {
        return orders;
    }
}
