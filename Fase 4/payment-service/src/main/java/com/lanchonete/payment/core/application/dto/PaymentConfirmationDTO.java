package com.lanchonete.payment.core.application.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentConfirmationDTO {
    private Long id;
    private Boolean liveMode;
    private String type;
    private Instant dateCreated;
    private Long userId;
    private String apiVersion;
    private String action;
    private PaymentConfirmationDataDTO data;

    @Data
    @Builder
    public static class PaymentConfirmationDataDTO{
        private String id;
    }
}

