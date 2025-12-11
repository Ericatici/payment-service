package com.lanchonete.order.core.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import com.lanchonete.order.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.order.core.domain.model.enums.PaymentStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long id;
    private String customerCpf;
    private String customerName;
    private List<OrderItem> items;
    private LocalDateTime orderDate;
    private OrderStatusEnum status;
    private PaymentStatusEnum paymentStatus;
    private String qrCodeData;
    private String paymentId;
    private BigDecimal totalPrice;
    private Instant createdDate;
    private Instant updatedDate;
}


