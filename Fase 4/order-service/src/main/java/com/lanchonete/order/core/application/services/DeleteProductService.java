package com.lanchonete.order.core.application.services;

import org.springframework.stereotype.Service;

import com.lanchonete.order.core.application.usecases.DeleteProductUseCase;
import com.lanchonete.order.core.domain.exceptions.ProductNotFoundException;
import com.lanchonete.order.core.domain.repositories.ProductRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class DeleteProductService implements DeleteProductUseCase {

    private final ProductRepository productRepository;

    @Override
    public void delete(String itemId) {
        log.info("Deleting product with ID: {}", itemId);
        
        productRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", itemId);
                    return new ProductNotFoundException("Product with ID " + itemId + " not found");
                });
        
        productRepository.deleteById(itemId);
        
        log.info("Product deleted successfully with ID: {}", itemId);
    }
}



