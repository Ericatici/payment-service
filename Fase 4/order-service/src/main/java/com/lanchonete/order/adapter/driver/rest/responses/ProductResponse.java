package com.lanchonete.order.adapter.driver.rest.responses;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.lanchonete.order.core.domain.model.enums.ProductCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProductResponse {

    @JsonProperty
    private String itemId;

    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private BigDecimal price;

    @JsonProperty
    private ProductCategoryEnum category;
    
}



