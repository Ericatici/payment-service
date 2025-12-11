package com.lanchonete.payment.adapter.driven.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lanchonete.payment.core.domain.model.PaymentData;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MPQrCodePaymentResponse {

    @JsonProperty
    private String id;

    @JsonProperty
    private String status;

    @JsonProperty("external_reference")
    private String externalReference;

    @JsonProperty("type_response")
    private TypeResponse typeResponse;

    @Data
    @Builder
    public static class TypeResponse {

        @JsonProperty("qr_data")
        private String qrData;
    }

    public PaymentData toPaymentData(){
        String qrCode = null;
        if (this.typeResponse != null){
            qrCode = this.typeResponse.getQrData();
        }

        return PaymentData.builder()
            .paymentId(this.id.toString())
            .qrCode(qrCode)
            .build();
    }
    
}

