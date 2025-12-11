package com.lanchonete.payment.adapter.driver.rest.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lanchonete.payment.core.domain.model.enums.PaymentStatusEnum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentStatusResponse {
    @JsonProperty
    private Long orderId;
    @JsonProperty
    private PaymentStatusEnum paymentStatus;
}

