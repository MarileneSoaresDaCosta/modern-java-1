package com.example.modernjava.analytics;

import com.example.modernjava.domain.Customer;
import com.example.modernjava.domain.TestDataGenerator;
import com.example.modernjava.repositories.OrderTestRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

class OrderAnalysisServiceTest {

    private static OrderAnalysisService analysisService;

    @BeforeAll
    static void beforeAll() {
        // Set up a test repository, which may seem unnecessary, but will be useful as
        // we migrate the test data into a database.
        var repository = new OrderTestRepository(TestDataGenerator.generateTestOrders());
        analysisService = new OrderAnalysisService(StreamSupport.stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList()));
        System.out.println("--- Initialized analyzer with test order data ---");
        System.out.println("--- Total Orders: " + analysisService.totalOrders());
        System.out.println("--- Total Units Sold: " + analysisService.totalUnitsSold());
        System.out.println("--- Total Revenue: " + "TODO: analysisService.totalRevenue()");
    }

    @Test
    void totalOrders() {
        assertThat(analysisService.totalOrders()).isNotZero();
    }

    @Test
    void totalUnitsSold() {
        assertThat(analysisService.totalUnitsSold()).isNotZero();
    }

    @Test
    void totalUnitsSoldByProduct() {
        // Ensure that the sum of units sold per product is equal to the total units sold
        assertThat(analysisService.totalUnitsSoldByProduct().values().stream().mapToInt(Integer::intValue)
                .sum()).isEqualTo(analysisService.totalUnitsSold());
    }

    @Test
    void totalUnitsSoldByCustomerByProduct() {
        // Ensure that the sum of units sold per customer/product is equal to the total units sold
        assertThat(analysisService.totalUnitsSoldByCustomerByProduct().values().stream()
                .flatMapToInt(e -> e.values().stream().mapToInt(Integer::intValue))
                .sum()).isEqualTo(analysisService.totalUnitsSold());
        // Ensure that the customers returned by totalUnitsSoldByCustomerByProduct includes all customers
        assertThat(analysisService.distinctCustomers().stream().map(Customer::getName)
                .toList()).containsExactlyInAnyOrderElementsOf(analysisService.totalUnitsSoldByCustomerByProduct()
                .keySet());
    }

    @Test
    void totalUnitsSoldByCountryByProduct() {
        // Ensure that the sum of units sold per country/product is equal to the total units sold
        assertThat(analysisService.totalUnitsSoldByCountryByProduct().values().stream()
                .flatMapToInt(e -> e.values().stream().mapToInt(Integer::intValue))
                .sum()).isEqualTo(analysisService.totalUnitsSold());
        // Ensure that the countries returned by totalUnitsSoldByCountryByProduct includes all countries
        assertThat(analysisService.distinctCountries()).containsExactlyInAnyOrderElementsOf(analysisService.totalUnitsSoldByCountryByProduct()
                .keySet());
    }

    // TODO: Implement the tests that should accompany the new functions added to the OrderAnalysisService
}