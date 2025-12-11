package com.lanchonete.order.adapter.driven.persistence.repositories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

import com.lanchonete.order.adapter.driven.persistence.repositories.mongo.MongoProductRepository;
import com.lanchonete.order.core.domain.model.Product;
import com.lanchonete.order.core.domain.model.enums.ProductCategoryEnum;
import com.lanchonete.order.mocks.ProductMock;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryImplTest {

    @Mock
    private MongoProductRepository mongoProductRepository;

    @InjectMocks
    private ProductRepositoryImpl productRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        product = ProductMock.createProductMock();
    }

    @Test
    void shouldSaveProductSuccessfully() {
        when(mongoProductRepository.save(any(Product.class))).thenReturn(product);

        Product result = productRepository.saveProduct(product);

        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
        assertEquals(product.getName(), result.getName());
        
        verify(mongoProductRepository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldFindProductByIdSuccessfully() {
        when(mongoProductRepository.findById(product.getId())).thenReturn(Optional.of(product));

        Optional<Product> result = productRepository.findById(product.getId());

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(product.getId(), result.get().getId());
        
        verify(mongoProductRepository, times(1)).findById(product.getId());
    }

    @Test
    void shouldReturnEmptyOptionalWhenProductNotFound() {
        String productId = "PROD-999";
        when(mongoProductRepository.findById(productId)).thenReturn(Optional.empty());

        Optional<Product> result = productRepository.findById(productId);

        assertTrue(result.isEmpty());
        
        verify(mongoProductRepository, times(1)).findById(productId);
    }

    @Test
    void shouldFindAllProductsSuccessfully() {
        List<Product> products = Arrays.asList(product);
        when(mongoProductRepository.findAll()).thenReturn(products);

        List<Product> result = productRepository.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        
        verify(mongoProductRepository, times(1)).findAll();
    }

    @Test
    void shouldFindProductsByCategorySuccessfully() {
        List<Product> products = Arrays.asList(product);
        when(mongoProductRepository.findByCategory(ProductCategoryEnum.LANCHE)).thenReturn(products);

        List<Product> result = productRepository.findByCategory(ProductCategoryEnum.LANCHE);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        
        verify(mongoProductRepository, times(1)).findByCategory(ProductCategoryEnum.LANCHE);
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        when(mongoProductRepository.findById(product.getId())).thenReturn(Optional.of(product));
        doNothing().when(mongoProductRepository).delete(any(Product.class));

        assertDoesNotThrow(() -> {
            productRepository.deleteById(product.getId());
        });
        
        verify(mongoProductRepository, times(1)).findById(product.getId());
        verify(mongoProductRepository, times(1)).delete(any(Product.class));
    }
}



