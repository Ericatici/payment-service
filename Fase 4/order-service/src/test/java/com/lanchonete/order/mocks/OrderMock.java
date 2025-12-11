package com.lanchonete.order.mocks;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.lanchonete.order.core.domain.model.Order;
import com.lanchonete.order.core.domain.model.OrderItem;
import com.lanchonete.order.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.order.core.domain.model.enums.PaymentStatusEnum;

public class OrderMock {

    public static Order createOrderMock() {
        return Order.builder()
                .id(1L)
                .customerCpf("12345678900")
                .items(createOrderItemsMock())
                .orderDate(LocalDateTime.now())
                .status(OrderStatusEnum.RECEIVED)
                .paymentStatus(PaymentStatusEnum.PENDING)
                .totalPrice(new BigDecimal("75.00"))
                .createdDate(Instant.now())
                .updatedDate(Instant.now())
                .build();
    }

    public static List<OrderItem> createOrderItemsMock() {
        OrderItem item1 = OrderItem.builder()
                .productId("PROD-001")
                .quantity(2)
                .itemPrice(new BigDecimal("25.00"))
                .createdDate(Instant.now())
                .updatedDate(Instant.now())
                .build();

        OrderItem item2 = OrderItem.builder()
                .productId("PROD-002")
                .quantity(1)
                .itemPrice(new BigDecimal("25.00"))
                .createdDate(Instant.now())
                .updatedDate(Instant.now())
                .build();

        return Arrays.asList(item1, item2);
    }

    public static Order createOrderWithoutItemsMock() {
        return Order.builder()
                .customerCpf("12345678900")
                .status(OrderStatusEnum.RECEIVED)
                .paymentStatus(PaymentStatusEnum.PENDING)
                .build();
    }
}


