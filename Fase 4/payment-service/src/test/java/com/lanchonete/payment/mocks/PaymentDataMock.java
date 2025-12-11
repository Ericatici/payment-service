package com.lanchonete.payment.mocks;

import com.lanchonete.payment.core.domain.model.PaymentData;

public class PaymentDataMock {

    public static PaymentData createPaymentDataMock() {
        return PaymentData.builder()
                .paymentId("mp-payment-123")
                .qrCode("00020126580014br.gov.bcb.pix")
                .build();
    }

    public static PaymentData createInvalidPaymentDataMock() {
        return PaymentData.builder()
                .paymentId(null)
                .qrCode(null)
                .build();
    }
}

