package com.lanchonete.production.core.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentConfirmationDTO {

    private String id;
    private String status;
    private Double totalAmount;
    
}

