package com.lanchonete.order.core.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lanchonete.order.core.domain.model.Product;
import com.lanchonete.order.core.domain.repositories.ProductRepository;
import com.lanchonete.order.mocks.ProductMock;

@ExtendWith(MockitoExtension.class)
class CreateProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CreateProductService createProductService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = ProductMock.createProductMock();
    }

    @Test
    void shouldCreateProductSuccessfully() {
        when(productRepository.saveProduct(any(Product.class))).thenReturn(product);

        Product result = createProductService.create(product);

        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
        assertEquals(product.getName(), result.getName());
        assertEquals(product.getPrice(), result.getPrice());
        
        verify(productRepository, times(1)).saveProduct(any(Product.class));
    }

    @Test
    void shouldGenerateIdWhenNotProvided() {
        Product productWithoutId = ProductMock.createProductMock();
        productWithoutId.setId(null);
        
        when(productRepository.saveProduct(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            if (p.getId() == null || p.getId().isEmpty()) {
                fail("ID should have been generated");
            }
            return p;
        });

        Product result = createProductService.create(productWithoutId);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertFalse(result.getId().isEmpty());
        
        verify(productRepository, times(1)).saveProduct(any(Product.class));
    }
}



