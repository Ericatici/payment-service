package com.lanchonete.payment.adapter.driven.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lanchonete.payment.core.domain.model.PaymentConfirmation;

import lombok.Data;

@Data
public class MPPaymentConfirmationResponse {

    @JsonProperty
    private String id;

    @JsonProperty
    private String status;

    @JsonProperty("total_amount")
    private Double totalAmount;
    

    public PaymentConfirmation toPaymentConfirmation(){
        return PaymentConfirmation.builder()
            .id(this.id)
            .status(this.status)
            .totalAmount(this.totalAmount)
            .build();
    }
    
}

