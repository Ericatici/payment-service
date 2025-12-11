package com.lanchonete.order.adapter.driver.rest.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lanchonete.order.adapter.driver.rest.requests.ProductRequest;
import com.lanchonete.order.core.application.usecases.CreateProductUseCase;
import com.lanchonete.order.core.application.usecases.DeleteProductUseCase;
import com.lanchonete.order.core.application.usecases.FindProductUseCase;
import com.lanchonete.order.core.application.usecases.UpdateProductUseCase;
import com.lanchonete.order.core.domain.exceptions.ProductNotFoundException;
import com.lanchonete.order.core.domain.model.Product;
import com.lanchonete.order.core.domain.model.enums.ProductCategoryEnum;
import com.lanchonete.order.mocks.ProductMock;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateProductUseCase createProductUseCase;

    @MockitoBean
    private FindProductUseCase findProductUseCase;

    @MockitoBean
    private UpdateProductUseCase updateProductUseCase;

    @MockitoBean
    private DeleteProductUseCase deleteProductUseCase;

    private Product product;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        product = ProductMock.createProductMock();
        
        productRequest = new ProductRequest();
        productRequest.setItemId("PROD-001");
        productRequest.setName("Hamburger");
        productRequest.setDescription("Delicious hamburger");
        productRequest.setPrice(new BigDecimal("25.00"));
        productRequest.setCategory(ProductCategoryEnum.LANCHE);
    }

    @Test
    void shouldCreateProductSuccessfully() throws Exception {
        when(createProductUseCase.create(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId").exists())
                .andExpect(jsonPath("$.name").value(product.getName()));

        verify(createProductUseCase, times(1)).create(any(Product.class));
    }

    @Test
    void shouldFindProductByIdSuccessfully() throws Exception {
        when(findProductUseCase.findProductById(product.getId())).thenReturn(product);

        mockMvc.perform(get("/products/{itemId}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(product.getId()))
                .andExpect(jsonPath("$.name").value(product.getName()));

        verify(findProductUseCase, times(1)).findProductById(product.getId());
    }

    @Test
    void shouldReturn404WhenProductNotFound() throws Exception {
        String productId = "PROD-999";
        when(findProductUseCase.findProductById(productId))
                .thenThrow(new ProductNotFoundException("Product not found"));

        mockMvc.perform(get("/products/{itemId}", productId))
                .andExpect(status().isNotFound());

        verify(findProductUseCase, times(1)).findProductById(productId);
    }

    @Test
    void shouldFindAllProductsSuccessfully() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(findProductUseCase.findAllProducts()).thenReturn(products);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemId").value(product.getId()));

        verify(findProductUseCase, times(1)).findAllProducts();
    }

    @Test
    void shouldUpdateProductSuccessfully() throws Exception {
        when(updateProductUseCase.update(anyString(), any(Product.class))).thenReturn(product);

        mockMvc.perform(put("/products/{itemId}", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(product.getId()));

        verify(updateProductUseCase, times(1)).update(anyString(), any(Product.class));
    }

    @Test
    void shouldDeleteProductSuccessfully() throws Exception {
        doNothing().when(deleteProductUseCase).delete(product.getId());

        mockMvc.perform(delete("/products/{itemId}", product.getId()))
                .andExpect(status().isNoContent());

        verify(deleteProductUseCase, times(1)).delete(product.getId());
    }
}



