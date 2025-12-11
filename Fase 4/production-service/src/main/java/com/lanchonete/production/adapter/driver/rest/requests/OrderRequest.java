package com.lanchonete.production.adapter.driver.rest.requests;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    private String customerCpf;

    @Valid
    @NotEmpty(message = "Items list cannot be empty")
    private List<OrderItemRequest> items;

    @Data
    @Builder
    public static class OrderItemRequest {
        @NotEmpty(message = "Product ID is required")
        private String productId;

        @NotEmpty(message = "Product ID is required")
        private String productName;

        @NotNull(message = "Quantity is required")
        @Min(value = 0)
        private Integer quantity;

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        private BigDecimal itemPrice;

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        private BigDecimal itemsTotalPrice;
    }
}

