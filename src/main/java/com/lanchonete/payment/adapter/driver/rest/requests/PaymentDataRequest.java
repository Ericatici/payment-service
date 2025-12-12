package com.lanchonete.payment.adapter.driver.rest.requests;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lanchonete.payment.core.application.dto.PaymentDataDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDataRequest {

    @JsonProperty("id")
    private Long orderId;

    @JsonProperty
    private BigDecimal totalPrice;
    public PaymentDataDTO toDto(){
        return PaymentDataDTO.builder()
            .orderId(this.orderId)
            .totalPrice(this.totalPrice)
            .build();
    }
    
}

