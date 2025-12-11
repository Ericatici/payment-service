package com.lanchonete.order.adapter.driven.persistence.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.lanchonete.order.core.domain.model.enums.ProductCategoryEnum;
import org.springframework.stereotype.Component;

import com.lanchonete.order.adapter.driven.persistence.repositories.mongo.MongoProductRepository;
import com.lanchonete.order.core.domain.model.Product;
import com.lanchonete.order.core.domain.repositories.ProductRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final MongoProductRepository mongoProductRepository;

    @Override
    public Product saveProduct(Product product) {
        if (product.getCreatedDate() == null) {
            product.setCreatedDate(Instant.now());
        }
        product.setUpdatedDate(Instant.now());
        return mongoProductRepository.save(product);
    }

    @Override
    public Optional<Product> findById(String itemId) {
        return mongoProductRepository.findById(itemId);
    }

    @Override
    public List<Product> findAll() {
        return mongoProductRepository.findAll();
    }

    @Override
    public List<Product> findByCategory(ProductCategoryEnum category) {
        return mongoProductRepository.findByCategory(category);
    }

    @Override
    public void deleteById(String itemId) {
        Optional<Product> products = mongoProductRepository.findById(itemId);
        if (products.isPresent()) {
            mongoProductRepository.delete(products.get());
        }
    }
    
}


