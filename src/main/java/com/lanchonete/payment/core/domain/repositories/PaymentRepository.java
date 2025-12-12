package com.lanchonete.payment.core.domain.repositories;

import java.math.BigDecimal;

import com.lanchonete.payment.core.domain.model.PaymentConfirmation;
import com.lanchonete.payment.core.domain.model.PaymentData;

public interface PaymentRepository {

    PaymentConfirmation getPaymentStatus(String paymentId);
    PaymentData getPaymentData(Long orderId, BigDecimal totalPrice);
    
}

