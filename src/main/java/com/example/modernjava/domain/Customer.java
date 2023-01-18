package com.example.modernjava.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple customer class
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    String name;
    Address billingAddress;
    Address shippingAddress;
}
