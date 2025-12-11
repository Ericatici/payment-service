package com.lanchonete.payment.mocks;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import com.lanchonete.payment.core.application.dto.OrderDTO;
import com.lanchonete.payment.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.payment.core.domain.model.enums.PaymentStatusEnum;

public class OrderDTOMock {

    public static OrderDTO createOrderDTOWithPayment() {
        return OrderDTO.builder()
                .id(1L)
                .customerCpf("12345678900")
                .orderDate(LocalDateTime.now())
                .status(OrderStatusEnum.RECEIVED)
                .paymentStatus(PaymentStatusEnum.PENDING)
                .qrCodeData("00020126580014br.gov.bcb.pix")
                .paymentId("mp-payment-123")
                .totalPrice(new BigDecimal("50.00"))
                .createdDate(Instant.now())
                .updatedDate(Instant.now())
                .build();
    }
}

