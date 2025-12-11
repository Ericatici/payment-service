package com.lanchonete.order.core.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lanchonete.order.core.domain.exceptions.ProductNotFoundException;
import com.lanchonete.order.core.domain.model.Product;
import com.lanchonete.order.core.domain.model.enums.ProductCategoryEnum;
import com.lanchonete.order.core.domain.repositories.ProductRepository;
import com.lanchonete.order.mocks.ProductMock;

@ExtendWith(MockitoExtension.class)
class FindProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private FindProductService findProductService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = ProductMock.createProductMock();
    }

    @Test
    void shouldFindProductByIdSuccessfully() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        Product result = findProductService.findProductById(product.getId());

        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
        assertEquals(product.getName(), result.getName());
        
        verify(productRepository, times(1)).findById(product.getId());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        String productId = "PROD-999";
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            findProductService.findProductById(productId);
        });

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void shouldFindAllProductsSuccessfully() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = findProductService.findAllProducts();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(product.getId(), result.get(0).getId());
        
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void shouldFindProductsByCategorySuccessfully() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.findByCategory(ProductCategoryEnum.LANCHE)).thenReturn(products);

        List<Product> result = findProductService.findProductsByCategory(ProductCategoryEnum.LANCHE);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        
        verify(productRepository, times(1)).findByCategory(ProductCategoryEnum.LANCHE);
    }
}



