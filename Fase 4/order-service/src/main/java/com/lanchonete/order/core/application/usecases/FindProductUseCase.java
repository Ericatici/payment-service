package com.lanchonete.order.core.application.usecases;

import java.util.List;

import com.lanchonete.order.core.domain.model.Product;
import com.lanchonete.order.core.domain.model.enums.ProductCategoryEnum;

public interface FindProductUseCase {
    Product findProductById(String itemId);
    List<Product> findAllProducts();
    List<Product> findProductsByCategory(ProductCategoryEnum category);
}



