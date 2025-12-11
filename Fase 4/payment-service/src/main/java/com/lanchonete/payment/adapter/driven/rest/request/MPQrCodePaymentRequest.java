package com.lanchonete.payment.adapter.driven.rest.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MPQrCodePaymentRequest {

    @JsonProperty
    private String type;

    @JsonProperty("total_amount")
    private String totalAmount;

    @JsonProperty
    private String description;

    @JsonProperty("external_reference")
    private String externalReference;    

    @JsonProperty
    private Config config;

    @JsonProperty
    private Transactions transactions;

    @Data
    @Builder
    public static class Config {

        @JsonProperty
        private Qr qr;

        @Data
        @Builder
        public static class Qr {

            @JsonProperty("external_pos_id")
            private String externalPosId;  

            @JsonProperty
             private String mode;

        }

    }

    @Data
    @Builder
    public static class Transactions {

        @JsonProperty
        private List<Payments> payments;

        @Data
        @Builder
        public static class Payments {

            @JsonProperty
             private String amount;

        }
    }
    
}

