package com.lanchonete.customer.adapter.driver.rest.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class CustomerResponse {

    @JsonProperty
    private String cpf;

    @JsonProperty
    private String name;

    @JsonProperty
    private String email;
    
}


