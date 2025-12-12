package com.lanchonete.payment.adapter.driver.rest.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentDataResponse {

    @JsonProperty
    private String qrCode;

    @JsonProperty
    private String paymentId;
    
}
