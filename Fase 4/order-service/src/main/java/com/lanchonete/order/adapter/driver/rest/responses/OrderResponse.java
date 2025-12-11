package com.lanchonete.order.adapter.driver.rest.responses;

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
public class OrderResponse {

    private Long id;
    private String customerCpf;
    private String customerName;
    private List<OrderItemResponse> items;
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
    public static class OrderItemResponse {
        private String productId;
        private String productName;
        private Integer quantity;
        private BigDecimal itemPrice;
        private BigDecimal itemsTotalPrice;
    }
}


