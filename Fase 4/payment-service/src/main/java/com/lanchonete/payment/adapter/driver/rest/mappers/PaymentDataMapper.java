package com.lanchonete.payment.adapter.driver.rest.mappers;

import com.lanchonete.payment.adapter.driver.rest.responses.PaymentDataResponse;
import com.lanchonete.payment.core.domain.model.PaymentData;

public class PaymentDataMapper {

    public static PaymentDataResponse toResponse(PaymentData paymentData) {
        return PaymentDataResponse.builder()
                .qrCode(paymentData.getQrCode())
                .paymentId(paymentData.getPaymentId())
                .build();
    }
}

