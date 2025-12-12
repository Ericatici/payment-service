package com.lanchonete.payment.core.application.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentDataDTO {
    private Long orderId;
    private BigDecimal totalPrice;

}

