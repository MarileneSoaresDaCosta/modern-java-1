package com.example.modernjava.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Simple order class
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    Customer customer;
    Address altShippingAddress;
    List<OrderItem> items;
}
