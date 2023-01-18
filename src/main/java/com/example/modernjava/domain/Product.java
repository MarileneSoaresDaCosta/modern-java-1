package com.example.modernjava.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Simple product class
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    String sku;
    String name;
    String description;
    BigDecimal price;
}
