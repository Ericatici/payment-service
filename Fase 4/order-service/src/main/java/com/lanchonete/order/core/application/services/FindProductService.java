package com.lanchonete.order.core.application.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lanchonete.order.core.application.usecases.FindProductUseCase;
import com.lanchonete.order.core.domain.exceptions.ProductNotFoundException;
import com.lanchonete.order.core.domain.model.Product;
import com.lanchonete.order.core.domain.model.enums.ProductCategoryEnum;
import com.lanchonete.order.core.domain.repositories.ProductRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class FindProductService implements FindProductUseCase {

    private final ProductRepository productRepository;

    @Override
    public Product findProductById(String itemId) {
        log.info("Finding product by ID: {}", itemId);
        
        Product product = productRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", itemId);
                    return new ProductNotFoundException("Product with ID " + itemId + " not found");
                });
        
        log.info("Product found: {}", product.getName());
        return product;
    }

    @Override
    public List<Product> findAllProducts() {
        log.info("Finding all products");
        
        List<Product> products = productRepository.findAll();
        
        log.info("Found {} products", products.size());
        return products;
    }

    @Override
    public List<Product> findProductsByCategory(ProductCategoryEnum category) {
        log.info("Finding products by category: {}", category);
        
        List<Product> products = productRepository.findByCategory(category);
        
        log.info("Found {} products in category {}", products.size(), category);
        return products;
    }
}



