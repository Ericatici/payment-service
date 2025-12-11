package com.lanchonete.production.core.application.services;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lanchonete.production.core.domain.model.OrderItem;
import com.lanchonete.production.mocks.OrderMock;

@ExtendWith(MockitoExtension.class)
class CreateOrderItemServiceTest {

    @InjectMocks
    private CreateOrderItemService createOrderItemService;

    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        orderItem = OrderMock.createOrderItemMock();
    }

    @Test
    void shouldCreateOrderItemSuccessfully() {
        OrderItem result = createOrderItemService.createOrderItem(orderItem);
        assertNotNull(result);
        assertEquals(orderItem.getProductId(), result.getProductId());
        assertEquals(orderItem.getProductName(), result.getProductName());
        assertEquals(orderItem.getQuantity(), result.getQuantity());
        assertEquals(orderItem.getItemPrice(), result.getItemPrice());
        assertNotNull(result.getItemsTotalPrice());
    }

    @Test
    void shouldCalculateItemsTotalPriceCorrectly() {
        OrderItem item = OrderItem.builder()
                .productId("PROD-001")
                .quantity(3)
                .itemPrice(new BigDecimal("10.00"))
                .build();
        OrderItem result = createOrderItemService.createOrderItem(item);
        assertNotNull(result);
        assertEquals(new BigDecimal("30.00"), result.getItemsTotalPrice());
    }

    @Test
    void shouldHandleZeroQuantity() {
        OrderItem item = OrderItem.builder()
                .productId("PROD-001")
                .quantity(0)
                .itemPrice(new BigDecimal("10.00"))
                .build();
        OrderItem result = createOrderItemService.createOrderItem(item);
        assertNotNull(result);
        assertEquals(0, result.getItemsTotalPrice().compareTo(BigDecimal.ZERO));
    }

    @Test
    void shouldHandleNullProductName() {
        OrderItem item = OrderItem.builder()
                .productId("PROD-001")
                .productName(null)
                .quantity(2)
                .itemPrice(new BigDecimal("25.00"))
                .build();
        OrderItem result = createOrderItemService.createOrderItem(item);
        assertNotNull(result);
        assertNull(result.getProductName());
    }
}

