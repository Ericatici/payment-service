package com.lanchonete.production.adapter.driver.rest.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lanchonete.production.core.application.dto.PaymentConfirmationDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentConfirmationRequest {

    @JsonProperty
    private String id;
    @JsonProperty
    private String status;
    @JsonProperty
    private Double totalAmount;

    public PaymentConfirmationDTO toDto(){
        return PaymentConfirmationDTO.builder()
            .id(this.id)
            .status(this.status)
            .totalAmount(this.totalAmount)
            .build();
    }
    
}

