package com.lanchonete.order.mocks;

import java.math.BigDecimal;
import java.time.Instant;

import com.lanchonete.order.core.domain.model.Product;
import com.lanchonete.order.core.domain.model.enums.ProductCategoryEnum;

public class ProductMock {

    public static Product createProductMock() {
        return Product.builder()
                .id("PROD-001")
                .name("Hamburger")
                .description("Delicious hamburger")
                .price(new BigDecimal("25.00"))
                .category(ProductCategoryEnum.LANCHE)
                .createdDate(Instant.now())
                .updatedDate(Instant.now())
                .build();
    }

    public static Product createProductMock(String itemId, String name, BigDecimal price) {
        return Product.builder()
                .id(itemId)
                .name(name)
                .description("Product description")
                .price(price)
                .category(ProductCategoryEnum.LANCHE)
                .createdDate(Instant.now())
                .updatedDate(Instant.now())
                .build();
    }
}



