package com.lanchonete.order.core.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lanchonete.order.core.domain.exceptions.ProductNotFoundException;
import com.lanchonete.order.core.domain.model.Product;
import com.lanchonete.order.core.domain.repositories.ProductRepository;
import com.lanchonete.order.mocks.ProductMock;

@ExtendWith(MockitoExtension.class)
class UpdateProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UpdateProductService updateProductService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = ProductMock.createProductMock();
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        Product updatedProduct = ProductMock.createProductMock();
        updatedProduct.setName("Updated Hamburger");
        updatedProduct.setPrice(new BigDecimal("30.00"));
        
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.saveProduct(any(Product.class))).thenReturn(updatedProduct);

        Product result = updateProductService.update(product.getId(), updatedProduct);

        assertNotNull(result);
        assertEquals(updatedProduct.getName(), result.getName());
        assertEquals(updatedProduct.getPrice(), result.getPrice());
        
        verify(productRepository, times(1)).findById(product.getId());
        verify(productRepository, times(1)).saveProduct(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        String productId = "PROD-999";
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            updateProductService.update(productId, product);
        });

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).saveProduct(any(Product.class));
    }
}



