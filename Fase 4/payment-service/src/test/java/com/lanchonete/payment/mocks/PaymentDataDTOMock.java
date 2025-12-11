package com.lanchonete.payment.mocks;

import java.math.BigDecimal;

import com.lanchonete.payment.core.application.dto.PaymentDataDTO;

public class PaymentDataDTOMock {

    public static PaymentDataDTO createPaymentDataMock() {
        return PaymentDataDTO.builder()
                .orderId(1L)
                .totalPrice(new BigDecimal("50.00"))
                .build();
    }

}

