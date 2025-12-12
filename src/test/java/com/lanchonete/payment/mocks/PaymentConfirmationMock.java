package com.lanchonete.payment.mocks;

import com.lanchonete.payment.core.domain.model.PaymentConfirmation;

public class PaymentConfirmationMock {

    public static PaymentConfirmation createApprovedPaymentMock() {
        return PaymentConfirmation.builder()
                .id("mp-payment-123")
                .status("processed")
                .totalAmount(50.0)
                .build();
    }
}

