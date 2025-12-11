package com.lanchonete.payment.core.domain.model;

import com.lanchonete.payment.core.domain.model.enums.PaymentStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PaymentStatus {

    private Long orderId;
    private PaymentStatusEnum paymentStatus;
    
}

