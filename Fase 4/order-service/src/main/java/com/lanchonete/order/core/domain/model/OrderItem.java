package com.lanchonete.order.core.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal itemPrice;
    private BigDecimal itemsTotalPrice;
    private Instant createdDate;
    private Instant updatedDate;
}


