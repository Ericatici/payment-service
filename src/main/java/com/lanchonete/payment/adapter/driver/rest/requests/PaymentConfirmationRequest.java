package com.lanchonete.payment.adapter.driver.rest.requests;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lanchonete.payment.core.application.dto.PaymentConfirmationDTO;
import com.lanchonete.payment.core.application.dto.PaymentConfirmationDTO.PaymentConfirmationDataDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentConfirmationRequest {

    @JsonProperty("id") 
    private Long id;

    @JsonProperty("live_mode")
    private Boolean liveMode;

    @JsonProperty("type")
    private String type;

    @JsonProperty("date_created")
    private Instant dateCreated;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("api_version")
    private String apiVersion;

    @JsonProperty("action")
    private String action;

    @JsonProperty("data") 
    private PaymentConfirmationDataRequest data;

    @Data
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentConfirmationDataRequest {
        @JsonProperty("id") 
        private String id;

        public PaymentConfirmationDataDTO toDto(){
            return PaymentConfirmationDataDTO.builder()
                .id(this.id)
                .build();
        }
    }
    public PaymentConfirmationDTO toDto(){
        return PaymentConfirmationDTO.builder()
            .id(this.id)
            .liveMode(this.liveMode)
            .type(this.type)
            .dateCreated(this.dateCreated)
            .userId(this.userId)
            .apiVersion(this.apiVersion)
            .action(this.action)
            .data(this.data.toDto())
            .build();
    }
    
}

