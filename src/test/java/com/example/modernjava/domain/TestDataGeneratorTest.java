package com.example.modernjava.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestDataGeneratorTest {
    @Test
    void testDataGeneratorGeneratesConsistentData() {
        var orders1 = TestDataGenerator.generateTestOrders(false);
        var orders2 = TestDataGenerator.generateTestOrders(false);
        assertThat(orders1).containsExactlyInAnyOrderElementsOf(orders2);
    }
}
