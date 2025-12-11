package com.lanchonete.order.core.application.services;

import org.springframework.stereotype.Service;

import com.lanchonete.order.core.application.usecases.UpdateProductUseCase;
import com.lanchonete.order.core.domain.exceptions.ProductNotFoundException;
import com.lanchonete.order.core.domain.model.Product;
import com.lanchonete.order.core.domain.repositories.ProductRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class UpdateProductService implements UpdateProductUseCase {

    private final ProductRepository productRepository;

    @Override
    public Product update(String itemId, Product product) {
        log.info("Updating product with ID: {}", itemId);
        
        productRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", itemId);
                    return new ProductNotFoundException("Product with ID " + itemId + " not found");
                });
        
        product.setId(itemId);
        
        Product updatedProduct = productRepository.saveProduct(product);
        
        log.info("Product updated successfully: {}", updatedProduct.getName());
        return updatedProduct;
    }
}



