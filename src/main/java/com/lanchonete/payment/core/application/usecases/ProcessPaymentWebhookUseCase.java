package com.lanchonete.payment.core.application.usecases;

import com.lanchonete.payment.core.application.dto.PaymentConfirmationDTO;

public interface ProcessPaymentWebhookUseCase {
    void processPaymentConfirmation(PaymentConfirmationDTO paymentConfirmationDTO);
}

