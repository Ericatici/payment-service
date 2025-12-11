package com.lanchonete.order.adapter.driven.persistence.repositories.mongo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.lanchonete.order.core.domain.model.Product;
import com.lanchonete.order.core.domain.model.enums.ProductCategoryEnum;

@Repository
public interface MongoProductRepository extends MongoRepository<Product, String> {
    List<Product> findByCategory(ProductCategoryEnum category);
    Optional<Product> findById(String itemId);
}



