package com.lanchonete.payment.core.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import com.lanchonete.payment.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.payment.core.domain.model.enums.PaymentStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private String customerCpf;
    private LocalDateTime orderDate;
    private OrderStatusEnum status;
    private PaymentStatusEnum paymentStatus;
    private String qrCodeData;
    private String paymentId;
    private BigDecimal totalPrice;
    private Instant createdDate;
    private Instant updatedDate;
}

