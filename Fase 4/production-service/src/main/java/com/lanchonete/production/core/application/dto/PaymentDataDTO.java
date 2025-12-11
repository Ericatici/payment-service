package com.lanchonete.production.core.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentDataDTO {

    private String qrCode;
    private String paymentId;

}

