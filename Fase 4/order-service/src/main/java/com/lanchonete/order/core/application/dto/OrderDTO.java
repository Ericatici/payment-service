package com.lanchonete.order.core.application.dto;

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
public class OrderDTO {

    private Long id;
    private String customerCpf;
    private String customerName;
    private List<OrderItemDTO> items;
    private LocalDateTime orderDate;
    private OrderStatusEnum status;
    private PaymentStatusEnum paymentStatus;
    private String qrCodeData;
    private String paymentId;
    private BigDecimal totalPrice;
    private Instant createdDate;
    private Instant updatedDate;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private String productId;
        private String productName;
        private Integer quantity;
        private BigDecimal itemPrice;
        private BigDecimal itemsTotalPrice;
    }
}


