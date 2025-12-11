package com.lanchonete.production.mocks;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.OrderItem;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.production.core.domain.model.enums.PaymentStatusEnum;

public class OrderMock {

    public static Order createOrderMock() {
        List<OrderItem> items = new ArrayList<>();
        items.add(createOrderItemMock());
        
        return Order.builder()
                .id(1L)
                .customerCpf("12345678900")
                .items(items)
                .orderDate(LocalDateTime.now())
                .status(OrderStatusEnum.RECEIVED)
                .paymentStatus(PaymentStatusEnum.PENDING)
                .qrCodeData(null)
                .paymentId(null)
                .totalPrice(new BigDecimal("50.00"))
                .createdDate(Instant.now())
                .updatedDate(Instant.now())
                .build();
    }

    public static OrderItem createOrderItemMock() {
        return OrderItem.builder()
                .id(1L)
                .productId("PROD-001")
                .quantity(2)
                .itemPrice(new BigDecimal("25.00"))
                .createdDate(Instant.now())
                .updatedDate(Instant.now())
                .build();
    }

    public static Order createOrderWithPayment() {
        Order order = createOrderMock();
        order.setPaymentId("mp-payment-123");
        order.setQrCodeData("00020126580014br.gov.bcb.pix");
        return order;
    }
}

