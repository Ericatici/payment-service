package com.lanchonete.payment.core.application.usecases;

import com.lanchonete.payment.core.domain.model.PaymentStatus;

public interface ConsultPaymentStatusUseCase {
    PaymentStatus getPaymentStatus(Long orderId);
}

