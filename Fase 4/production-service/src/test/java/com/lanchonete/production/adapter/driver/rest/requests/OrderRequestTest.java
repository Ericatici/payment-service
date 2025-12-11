package com.lanchonete.production.adapter.driver.rest.requests;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class OrderRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidOrderRequest() {
        List<OrderRequest.OrderItemRequest> items = new ArrayList<>();
        OrderRequest.OrderItemRequest item = OrderRequest.OrderItemRequest.builder()
                .productId("PROD-001")
                .productName("Product 1")
                .quantity(2)
                .itemPrice(new BigDecimal("10.50"))
                .itemsTotalPrice(new BigDecimal("21.00"))
                .build();
        items.add(item);

        OrderRequest request = OrderRequest.builder()
                .customerCpf("12345678900")
                .items(items)
                .build();

        assertNotNull(request);
        assertEquals("12345678900", request.getCustomerCpf());
        assertEquals(1, request.getItems().size());
        assertEquals("PROD-001", request.getItems().get(0).getProductId());
    }

    @Test
    void shouldFailValidationWhenItemsListIsEmpty() {
        OrderRequest request = OrderRequest.builder()
                .customerCpf("12345678900")
                .items(new ArrayList<>())
                .build();

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Items list cannot be empty")));
    }

    @Test
    void shouldFailValidationWhenItemsListIsNull() {
        OrderRequest request = OrderRequest.builder()
                .customerCpf("12345678900")
                .items(null)
                .build();

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Items list cannot be empty")));
    }

    @Test
    void shouldFailValidationWhenProductIdIsEmpty() {
        List<OrderRequest.OrderItemRequest> items = new ArrayList<>();
        OrderRequest.OrderItemRequest item = OrderRequest.OrderItemRequest.builder()
                .productId("")
                .productName("Product 1")
                .quantity(2)
                .itemPrice(new BigDecimal("10.50"))
                .itemsTotalPrice(new BigDecimal("21.00"))
                .build();
        items.add(item);

        OrderRequest request = OrderRequest.builder()
                .customerCpf("12345678900")
                .items(items)
                .build();

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenQuantityIsNegative() {
        List<OrderRequest.OrderItemRequest> items = new ArrayList<>();
        OrderRequest.OrderItemRequest item = OrderRequest.OrderItemRequest.builder()
                .productId("PROD-001")
                .productName("Product 1")
                .quantity(-1)
                .itemPrice(new BigDecimal("10.50"))
                .itemsTotalPrice(new BigDecimal("21.00"))
                .build();
        items.add(item);

        OrderRequest request = OrderRequest.builder()
                .customerCpf("12345678900")
                .items(items)
                .build();

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenItemPriceIsNegative() {
        List<OrderRequest.OrderItemRequest> items = new ArrayList<>();
        OrderRequest.OrderItemRequest item = OrderRequest.OrderItemRequest.builder()
                .productId("PROD-001")
                .productName("Product 1")
                .quantity(2)
                .itemPrice(new BigDecimal("-10.50"))
                .itemsTotalPrice(new BigDecimal("21.00"))
                .build();
        items.add(item);

        OrderRequest request = OrderRequest.builder()
                .customerCpf("12345678900")
                .items(items)
                .build();

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldCreateOrderItemRequestWithBuilder() {
        OrderRequest.OrderItemRequest item = OrderRequest.OrderItemRequest.builder()
                .productId("PROD-001")
                .productName("Product 1")
                .quantity(2)
                .itemPrice(new BigDecimal("10.50"))
                .itemsTotalPrice(new BigDecimal("21.00"))
                .build();

        assertNotNull(item);
        assertEquals("PROD-001", item.getProductId());
        assertEquals("Product 1", item.getProductName());
        assertEquals(2, item.getQuantity());
        assertEquals(new BigDecimal("10.50"), item.getItemPrice());
        assertEquals(new BigDecimal("21.00"), item.getItemsTotalPrice());
    }
}

