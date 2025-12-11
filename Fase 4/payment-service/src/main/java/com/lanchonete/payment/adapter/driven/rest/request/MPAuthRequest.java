package com.lanchonete.payment.adapter.driven.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MPAuthRequest {

    @JsonProperty("client_secret")
    private String clientSecret;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("grant_type")
    private String granType;
    
}

