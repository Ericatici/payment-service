package com.lanchonete.payment.core.application.usecases;

import com.lanchonete.payment.core.application.dto.PaymentDataDTO;
import com.lanchonete.payment.core.domain.model.PaymentData;

public interface GeneratePaymentQrCodeUseCase {
    PaymentData generatePaymentQrCode(PaymentDataDTO paymentDataDTO);
}

