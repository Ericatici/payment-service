package com.lanchonete.payment.core.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentData {

    private String qrCode;
    private String paymentId;
    
}

