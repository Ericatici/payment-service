package com.lanchonete.production.adapter.driver.rest.responses;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.production.core.domain.model.enums.PaymentStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class OrderResponse {
   
    @JsonProperty
    private Long id;

    @JsonProperty
    private String customerCpf;

    @JsonProperty
    private String customerName;

    @JsonProperty
    private List<OrderItemResponse> items;

    @JsonProperty
    private LocalDateTime orderDate;

    @JsonProperty
    private OrderStatusEnum status;

    @JsonProperty
    private PaymentStatusEnum paymentStatus;

    @JsonProperty
    private String qrCodeData;

    @JsonProperty
    private BigDecimal totalPrice;

    @JsonProperty
    private String paymentId;

    @JsonProperty
    private Instant createdDate;

    @JsonProperty
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

