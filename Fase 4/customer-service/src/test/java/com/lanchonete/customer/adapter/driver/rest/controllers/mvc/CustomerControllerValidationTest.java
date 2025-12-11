package com.lanchonete.customer.adapter.driver.rest.controllers.mvc;

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
class CustomerControllerValidationTest {

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
    void shouldRejectCustomerWithNullCpf() throws Exception {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .cpf(null)
                .name("Test User")
                .email("test@example.com")
                .build();

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCustomerWithEmptyCpf() throws Exception {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .cpf("")
                .name("Test User")
                .email("test@example.com")
                .build();

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCustomerWithShortCpf() throws Exception {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .cpf("1234567890")
                .name("Test User")
                .email("test@example.com")
                .build();

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCustomerWithLongCpf() throws Exception {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .cpf("123456789012")
                .name("Test User")
                .email("test@example.com")
                .build();

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCustomerWithNullName() throws Exception {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .cpf("12345678900")
                .name(null)
                .email("test@example.com")
                .build();

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCustomerWithEmptyName() throws Exception {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .cpf("12345678900")
                .name("")
                .email("test@example.com")
                .build();

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCustomerWithNullEmail() throws Exception {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .cpf("12345678900")
                .name("Test User")
                .email(null)
                .build();

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCustomerWithEmptyEmail() throws Exception {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .cpf("12345678900")
                .name("Test User")
                .email("")
                .build();

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCustomerWithInvalidEmailFormat() throws Exception {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .cpf("12345678900")
                .name("Test User")
                .email("invalid-email")
                .build();

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCustomerWithEmailMissingAt() throws Exception {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .cpf("12345678900")
                .name("Test User")
                .email("testexample.com")
                .build();

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCustomerWithEmailMissingDomain() throws Exception {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .cpf("12345678900")
                .name("Test User")
                .email("test@")
                .build();

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCustomerWithEmailMissingLocalPart() throws Exception {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .cpf("12345678900")
                .name("Test User")
                .email("@example.com")
                .build();

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCustomerWithEmailWithDoubleDots() throws Exception {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .cpf("12345678900")
                .name("Test User")
                .email("test..test@example.com")
                .build();

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectRequestWithMalformedJson() throws Exception {
        String malformedJson = "{ \"cpf\": \"12345678900\", \"name\": \"Test User\", \"email\": \"test@example.com\"";

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldRejectRequestWithEmptyBody() throws Exception {
        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectRequestWithInvalidContentType() throws Exception {
        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.TEXT_PLAIN)
                .content("invalid content"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void shouldRejectRequestWithMissingContentType() throws Exception {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .cpf("12345678900")
                .name("Test User")
                .email("test@example.com")
                .build();

        mockMvc.perform(post("/customer/save")
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isUnsupportedMediaType());
    }
}