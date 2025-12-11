package com.lanchonete.production.core.application.usecases;

import com.lanchonete.production.core.domain.model.Order;

public interface GeneratePaymentQrCodeUseCase {
    Order generatePaymentQrCode(Order order);
}
