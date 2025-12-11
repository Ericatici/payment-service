package com.lanchonete.order.core.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DeleteProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private DeleteProductService deleteProductService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = ProductMock.createProductMock();
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        doNothing().when(productRepository).deleteById(product.getId());

        assertDoesNotThrow(() -> {
            deleteProductService.delete(product.getId());
        });

        verify(productRepository, times(1)).findById(product.getId());
        verify(productRepository, times(1)).deleteById(product.getId());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        String productId = "PROD-999";
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            deleteProductService.delete(productId);
        });

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).deleteById(anyString());
    }
}



