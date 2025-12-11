package com.lanchonete.production.core.application.usecases;

import com.lanchonete.production.core.application.dto.PaymentConfirmationDTO;

public interface UpdateOrderPaymentStatusUseCase {

    void updateOrderPaymentStatus(Long orderId, PaymentConfirmationDTO payment);
    
}
