package com.lanchonete.order.core.application.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lanchonete.order.core.application.usecases.CreateProductUseCase;
import com.lanchonete.order.core.domain.model.Product;
import com.lanchonete.order.core.domain.repositories.ProductRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CreateProductService implements CreateProductUseCase {

    private final ProductRepository productRepository;

    @Override
    public Product create(Product product) {
        log.info("Creating product: {}", product.getName());
        
        if (product.getId() == null || product.getId().isEmpty()) {
            product.setId(UUID.randomUUID().toString());
        }
        
        Product savedProduct = productRepository.saveProduct(product);
        
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return savedProduct;
    }
}



