package com.lanchonete.order.core.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.lanchonete.order.core.domain.model.Product;
import com.lanchonete.order.core.domain.model.enums.ProductCategoryEnum;

public interface ProductRepository {

    Product saveProduct(Product product);
    Optional<Product> findById(String itemId);
    List<Product> findAll();
    List<Product> findByCategory(ProductCategoryEnum category);
    void deleteById(String itemId);
    
}



