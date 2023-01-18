package com.example.modernjava.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DomainModelSerializationTest {
    static ObjectMapper objectMapper;
    static String TEST_ORDERS_JSON_FILE_NAME = "test_orders.json";
    @TempDir
    Path tempDir;

    @BeforeAll
    static void beforeAll() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void stringSerialization() throws JsonProcessingException {
        var ordersToSerialize = TestDataGenerator.generateTestOrders();
        String ordersJson = objectMapper.writeValueAsString(ordersToSerialize);
        var deserializedOrders = objectMapper.readValue(ordersJson, new TypeReference<List<Order>>() {
        });
        assertThat(deserializedOrders).containsExactlyInAnyOrderElementsOf(ordersToSerialize);
    }

    @Test
    void fileSerialization() throws IOException {
        Path testFilePath;
        testFilePath = tempDir.resolve(TEST_ORDERS_JSON_FILE_NAME);
        var testFile = testFilePath.toFile();
        var ordersToSerialize = TestDataGenerator.generateTestOrders();
        objectMapper.writeValue(testFile, ordersToSerialize);
        var deserializedOrders = objectMapper.readValue(testFile, new TypeReference<List<Order>>() {
        });
        assertThat(deserializedOrders).containsExactlyInAnyOrderElementsOf(ordersToSerialize);
    }

    @Test
    void resourceFileSerialization() throws IOException {
        var resourceFile = getClass().getClassLoader().getResource(TEST_ORDERS_JSON_FILE_NAME);
        var orders = TestDataGenerator.generateTestOrders();
        var deserializedOrders = objectMapper.readValue(resourceFile, new TypeReference<List<Order>>() {
        });
        assertThat(deserializedOrders).containsExactlyInAnyOrderElementsOf(orders);
    }
}
