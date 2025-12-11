package com.lanchonete.production.core.application.ports;

import com.lanchonete.production.core.application.dto.PaymentDataDTO;
import com.lanchonete.production.core.domain.model.Order;

public interface PaymentServicePort {
    PaymentDataDTO getPaymentData(Order order, String requestTraceId);
}

