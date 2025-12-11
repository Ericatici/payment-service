package com.lanchonete.order.core.application.usecases;

import com.lanchonete.order.core.domain.model.Product;

public interface CreateProductUseCase {
    Product create(Product product);
}



