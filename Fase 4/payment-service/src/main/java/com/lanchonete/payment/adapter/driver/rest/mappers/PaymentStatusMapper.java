package com.lanchonete.payment.adapter.driver.rest.mappers;

import com.lanchonete.payment.adapter.driver.rest.responses.PaymentStatusResponse;
import com.lanchonete.payment.core.domain.model.PaymentStatus;

public class PaymentStatusMapper {

    public static PaymentStatusResponse toResponse(PaymentStatus paymentStatus) {
        return PaymentStatusResponse.builder()
                .orderId(paymentStatus.getOrderId())
                .paymentStatus(paymentStatus.getPaymentStatus())
                .build();
    }
}

