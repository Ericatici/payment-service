package com.lanchonete.order.bdd;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.lanchonete.order.core.application.services.CreateProductService;
import com.lanchonete.order.core.application.services.FindProductService;
import com.lanchonete.order.core.domain.exceptions.ProductNotFoundException;
import com.lanchonete.order.core.domain.model.Product;
import com.lanchonete.order.core.domain.model.enums.ProductCategoryEnum;
import com.lanchonete.order.core.domain.repositories.ProductRepository;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
public class ProductManagementStepDefinitions {

    @MockitoBean
    private ProductRepository productRepository;

    @Autowired
    private CreateProductService createProductService;

    @Autowired
    private FindProductService findProductService;

    private Product product;
    private Product createdProduct;
    private Product foundProduct;
    private Exception thrownException;
    private String productId;

    @Given("I have a product with name {string}, description {string}, price {double} and category {string}")
    public void i_have_a_product_with_details(String name, String description, Double price, String category) {
        product = Product.builder()
                .name(name)
                .description(description)
                .price(new BigDecimal(price))
                .category(ProductCategoryEnum.valueOf(category))
                .build();
        
        Product savedProduct = Product.builder()
                .id("PROD-001")
                .name(name)
                .description(description)
                .price(new BigDecimal(price))
                .category(ProductCategoryEnum.valueOf(category))
                .build();
        
        when(productRepository.saveProduct(any(Product.class))).thenReturn(savedProduct);
    }

    @When("I create the product")
    public void i_create_the_product() {
        createdProduct = createProductService.create(product);
    }

    @Then("the product should be created successfully")
    public void the_product_should_be_created_successfully() {
        assertNotNull(createdProduct);
        verify(productRepository, times(1)).saveProduct(any(Product.class));
    }

    @Then("the product should have an ID")
    public void the_product_should_have_an_id() {
        assertNotNull(createdProduct.getId());
        assertFalse(createdProduct.getId().isEmpty());
    }

    @Then("the product name should be {string}")
    public void the_product_name_should_be(String expectedName) {
        assertEquals(expectedName, createdProduct.getName());
    }

    @Given("a product with ID {string} exists in the system")
    public void a_product_with_id_exists_in_the_system(String id) {
        this.productId = id;
        Product existingProduct = Product.builder()
                .id(id)
                .name("Hamburger")
                .description("Delicious hamburger")
                .price(new BigDecimal("25.00"))
                .category(ProductCategoryEnum.LANCHE)
                .build();
        
        when(productRepository.findById(id)).thenReturn(java.util.Optional.of(existingProduct));
    }

    @When("I search for the product with ID {string}")
    public void i_search_for_the_product_with_id(String id) {
        try {
            foundProduct = findProductService.findProductById(id);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the product should be found")
    public void the_product_should_be_found() {
        assertNotNull(foundProduct);
        assertNull(thrownException);
    }

    @Then("the product ID should be {string}")
    public void the_product_id_should_be(String expectedId) {
        assertEquals(expectedId, foundProduct.getId());
    }

    @Given("no product with ID {string} exists in the system")
    public void no_product_with_id_exists_in_the_system(String id) {
        this.productId = id;
        when(productRepository.findById(id)).thenReturn(java.util.Optional.empty());
    }

    @Then("an error should occur indicating product not found")
    public void an_error_should_occur_indicating_product_not_found() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof ProductNotFoundException);
    }
}

