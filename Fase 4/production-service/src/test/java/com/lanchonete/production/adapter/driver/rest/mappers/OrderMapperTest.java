package com.lanchonete.production.adapter.driver.rest.mappers;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lanchonete.production.adapter.driver.rest.responses.OrderResponse;
import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.OrderItem;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.production.core.domain.model.enums.PaymentStatusEnum;

class OrderMapperTest {

    private Order order;
    private List<OrderItem> items;

    @BeforeEach
    void setUp() {
        items = new ArrayList<>();
        OrderItem item1 = OrderItem.builder()
                .productId("PROD-001")
                .productName("Product 1")
                .quantity(2)
                .itemPrice(new BigDecimal("10.50"))
                .itemsTotalPrice(new BigDecimal("21.00"))
                .build();
        OrderItem item2 = OrderItem.builder()
                .productId("PROD-002")
                .productName("Product 2")
                .quantity(1)
                .itemPrice(new BigDecimal("25.00"))
                .itemsTotalPrice(new BigDecimal("25.00"))
                .build();
        items.add(item1);
        items.add(item2);

        order = Order.builder()
                .id(1L)
                .customerCpf("12345678900")
                .customerName("John Doe")
                .items(items)
                .orderDate(LocalDateTime.now())
                .status(OrderStatusEnum.WAITING_PAYMENT)
                .paymentStatus(PaymentStatusEnum.PENDING)
                .paymentId("PAY-12345")
                .qrCodeData("00020126580014br.gov.bcb.pix")
                .totalPrice(new BigDecimal("46.00"))
                .createdDate(Instant.now())
                .updatedDate(Instant.now())
                .build();
    }

    @Test
    void shouldMapOrderToOrderResponseSuccessfully() {
        OrderResponse response = OrderMapper.toOrderResponse(order);

        assertNotNull(response);
        assertEquals(order.getId(), response.getId());
        assertEquals(order.getCustomerCpf(), response.getCustomerCpf());
        assertEquals(order.getCustomerName(), response.getCustomerName());
        assertEquals(order.getOrderDate(), response.getOrderDate());
        assertEquals(order.getStatus(), response.getStatus());
        assertEquals(order.getPaymentStatus(), response.getPaymentStatus());
        assertEquals(order.getPaymentId(), response.getPaymentId());
        assertEquals(order.getQrCodeData(), response.getQrCodeData());
        assertEquals(order.getTotalPrice(), response.getTotalPrice());
        assertEquals(order.getCreatedDate(), response.getCreatedDate());
        assertEquals(order.getUpdatedDate(), response.getUpdatedDate());
        assertNotNull(response.getItems());
        assertEquals(2, response.getItems().size());
    }

    @Test
    void shouldMapOrderItemsCorrectly() {
        OrderResponse response = OrderMapper.toOrderResponse(order);

        assertNotNull(response.getItems());
        assertEquals(2, response.getItems().size());

        OrderResponse.OrderItemResponse itemResponse1 = response.getItems().get(0);
        assertEquals("PROD-001", itemResponse1.getProductId());
        assertEquals("Product 1", itemResponse1.getProductName());
        assertEquals(2, itemResponse1.getQuantity());
        assertEquals(new BigDecimal("10.50"), itemResponse1.getItemPrice());
        assertEquals(new BigDecimal("21.00"), itemResponse1.getItemsTotalPrice());

        OrderResponse.OrderItemResponse itemResponse2 = response.getItems().get(1);
        assertEquals("PROD-002", itemResponse2.getProductId());
        assertEquals("Product 2", itemResponse2.getProductName());
        assertEquals(1, itemResponse2.getQuantity());
        assertEquals(new BigDecimal("25.00"), itemResponse2.getItemPrice());
        assertEquals(new BigDecimal("25.00"), itemResponse2.getItemsTotalPrice());
    }

    @Test
    void shouldReturnNullWhenOrderIsNull() {
        OrderResponse response = OrderMapper.toOrderResponse(null);

        assertNull(response);
    }

    @Test
    void shouldHandleOrderWithNullItems() {
        order.setItems(null);

        OrderResponse response = OrderMapper.toOrderResponse(order);

        assertNotNull(response);
        assertNotNull(response.getItems());
        assertTrue(response.getItems().isEmpty());
    }

    @Test
    void shouldHandleOrderWithEmptyItems() {
        order.setItems(new ArrayList<>());

        OrderResponse response = OrderMapper.toOrderResponse(order);

        assertNotNull(response);
        assertNotNull(response.getItems());
        assertTrue(response.getItems().isEmpty());
    }

    @Test
    void shouldMapOrderWithAllFieldsNull() {
        Order orderWithNulls = Order.builder()
                .id(null)
                .customerCpf(null)
                .customerName(null)
                .items(null)
                .orderDate(null)
                .status(null)
                .paymentStatus(null)
                .paymentId(null)
                .qrCodeData(null)
                .totalPrice(null)
                .createdDate(null)
                .updatedDate(null)
                .build();

        OrderResponse response = OrderMapper.toOrderResponse(orderWithNulls);

        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getCustomerCpf());
        assertNull(response.getCustomerName());
        assertNotNull(response.getItems());
        assertTrue(response.getItems().isEmpty());
        assertNull(response.getOrderDate());
        assertNull(response.getStatus());
        assertNull(response.getPaymentStatus());
        assertNull(response.getPaymentId());
        assertNull(response.getQrCodeData());
        assertNull(response.getTotalPrice());
        assertNull(response.getCreatedDate());
        assertNull(response.getUpdatedDate());
    }
}

