package com.lanchonete.customer.adapter.driver.rest.controllers.mvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lanchonete.customer.adapter.driver.rest.controllers.CustomerController;
import com.lanchonete.customer.adapter.driver.rest.requests.CustomerRequest;
import com.lanchonete.customer.core.application.usecases.CreateCustomerUseCase;
import com.lanchonete.customer.core.application.usecases.FindCustomerUseCase;
import com.lanchonete.customer.core.application.usecases.UpdateCustomerUseCase;

@WebMvcTest(CustomerController.class)
class CustomerControllerPerformanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateCustomerUseCase createCustomerUseCase;

    @MockitoBean
    private FindCustomerUseCase findCustomerUseCase;

    @MockitoBean
    private UpdateCustomerUseCase updateCustomerUseCase;

    @Test
    void shouldHandleRapidSequentialRequests() throws Exception {
        when(createCustomerUseCase.createCustomer(any())).thenThrow(new RuntimeException("Test error"));
        
        for (int i = 0; i < 5; i++) {
            CustomerRequest customerRequest = CustomerRequest.builder()
                    .cpf(String.format("%011d", 40000000000L + i))
                    .name("Sequential User " + i)
                    .email("sequential" + i + "@example.com")
                    .build();

            mockMvc.perform(post("/customer/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(customerRequest)))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Test
    void shouldHandleLargePayloadRequests() throws Exception {
        String longName = "Very Long Customer Name That Exceeds Normal Length " + "A".repeat(50);
        String longEmail = "very.long.email.address.that.might.cause.issues.but.should.be.valid@example.com";

        CustomerRequest customerRequest = CustomerRequest.builder()
                .cpf("55566677788")
                .name(longName)
                .email(longEmail)
                .build();

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleInvalidJsonGracefully() throws Exception {
        String invalidJson = "{ \"cpf\": \"12345678900\", \"name\": \"Test User\", \"email\": }";

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldHandleEmptyRequestBody() throws Exception {
        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleMalformedCpfInUrl() throws Exception {
        mockMvc.perform(get("/customer/invalid-cpf"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHandleMultipleRequestsInSequence() throws Exception {
        when(createCustomerUseCase.createCustomer(any())).thenThrow(new RuntimeException("Test error"));
        
        for (int i = 0; i < 3; i++) {
            CustomerRequest customerRequest = CustomerRequest.builder()
                    .cpf(String.format("%011d", 50000000000L + i))
                    .name("Test User " + i)
                    .email("test" + i + "@example.com")
                    .build();

            mockMvc.perform(post("/customer/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(customerRequest)))
                    .andExpect(status().isInternalServerError());
            mockMvc.perform(get("/customer/" + String.format("%011d", 50000000000L + i)))
                    .andExpect(status().isNotFound());
        }
    }
}