package com.lanchonete.order.adapter.driver.rest.controllers;

import java.util.List;

import com.lanchonete.order.core.domain.model.enums.ProductCategoryEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lanchonete.order.adapter.driver.rest.requests.ProductRequest;
import com.lanchonete.order.adapter.driver.rest.responses.ProductResponse;
import com.lanchonete.order.core.application.usecases.CreateProductUseCase;
import com.lanchonete.order.core.application.usecases.DeleteProductUseCase;
import com.lanchonete.order.core.application.usecases.FindProductUseCase;
import com.lanchonete.order.core.application.usecases.UpdateProductUseCase;
import com.lanchonete.order.core.domain.model.Product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Products", description = "Operations related to products")
@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final FindProductUseCase findProductUseCase;
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;

    @Operation(summary = "Get all products")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        log.info("Received request to list all products");
        List<Product> products = findProductUseCase.findAllProducts();
        List<ProductResponse> responses = products.stream()
                .map(this::toProductResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get product by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        log.info("Received request to get product with ID: {}", id);
        Product product = findProductUseCase.findProductById(id);
        return ResponseEntity.ok(toProductResponse(product));
    }

    @Operation(summary = "Get products by category")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable ProductCategoryEnum category) {
        log.info("Received request to list products by category: {}", category);
        List<Product> products = findProductUseCase.findProductsByCategory(category);
        List<ProductResponse> responses = products.stream()
                .map(this::toProductResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Create a new product")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        log.info("Received request to create product: {}", request.getName());
        Product product = Product.builder()
                .id(request.getItemId())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .build();
        
        Product saved = createProductUseCase.create(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(toProductResponse(saved));
    }

    @Operation(summary = "Update a product")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductRequest request) {
        log.info("Received request to update product with ID: {}", id);
        
        Product product = Product.builder()
                .id(id)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .build();
        
        Product updated = updateProductUseCase.update(id, product);
        return ResponseEntity.ok(toProductResponse(updated));
    }

    private ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .itemId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .build();
    }

    @Operation(summary = "Delete a product")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        log.info("Received request to delete product with ID: {}", id);
        
        deleteProductUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}



