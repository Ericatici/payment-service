package com.lanchonete.customer.adapter.driver.rest.controllers.mvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lanchonete.customer.adapter.driver.rest.controllers.CustomerController;
import com.lanchonete.customer.adapter.driver.rest.requests.CustomerRequest;
import com.lanchonete.customer.adapter.driver.rest.requests.CustomerUpdateRequest;
import com.lanchonete.customer.core.application.usecases.CreateCustomerUseCase;
import com.lanchonete.customer.core.application.usecases.FindCustomerUseCase;
import com.lanchonete.customer.core.application.usecases.UpdateCustomerUseCase;
import com.lanchonete.customer.core.domain.model.Customer;

@WebMvcTest(CustomerController.class)
class CustomerControllerIntegrationTest {

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
    void shouldCreateCustomerSuccessfully() throws Exception {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .cpf("12345678900")
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        Customer customer = Customer.builder()
                .cpf("12345678900")
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        when(createCustomerUseCase.createCustomer(any())).thenReturn(customer);
        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf").value("12345678900"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingCustomerWithInvalidData() throws Exception {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .cpf("123")
                .name("")
                .email("invalid-email")
                .build();
        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenCreatingCustomerWithMissingFields() throws Exception {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .cpf("12345678900")
                .build();
        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindCustomerByCpfSuccessfully() throws Exception {
        Customer customer = Customer.builder()
                .cpf("98765432100")
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .build();

        when(findCustomerUseCase.getCustomerOptionalByCpf("98765432100"))
                .thenReturn(Optional.of(customer));
        mockMvc.perform(get("/customer/98765432100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("98765432100"))
                .andExpect(jsonPath("$.name").value("Jane Smith"))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"));
    }

    @Test
    void shouldReturnNotFoundWhenCustomerDoesNotExist() throws Exception {
        when(findCustomerUseCase.getCustomerOptionalByCpf("99999999999"))
                .thenReturn(Optional.empty());
        mockMvc.perform(get("/customer/99999999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateCustomerSuccessfully() throws Exception {
        CustomerUpdateRequest updateRequest = CustomerUpdateRequest.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        Customer updatedCustomer = Customer.builder()
                .cpf("11122233344")
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        when(updateCustomerUseCase.updateCustomer(any())).thenReturn(updatedCustomer);
        mockMvc.perform(put("/customer/11122233344")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.cpf").value("11122233344"))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingCustomerWithInvalidData() throws Exception {
        CustomerUpdateRequest updateRequest = CustomerUpdateRequest.builder()
                .name("")
                .email("invalid-email")
                .build();
        mockMvc.perform(put("/customer/11122233344")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleRequestWithTraceId() throws Exception {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .cpf("55566677788")
                .name("Trace Test")
                .email("trace@example.com")
                .build();

        Customer customer = Customer.builder()
                .cpf("55566677788")
                .name("Trace Test")
                .email("trace@example.com")
                .build();

        when(createCustomerUseCase.createCustomer(any())).thenReturn(customer);
        mockMvc.perform(post("/customer/save")
                .header("X-Request-Trace-Id", "test-trace-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf").value("55566677788"));
    }

    @Test
    void shouldValidateCpfLength() throws Exception {
        CustomerRequest shortCpfRequest = CustomerRequest.builder()
                .cpf("123")
                .name("Test User")
                .email("test@example.com")
                .build();

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shortCpfRequest)))
                .andExpect(status().isBadRequest());
        CustomerRequest longCpfRequest = CustomerRequest.builder()
                .cpf("123456789012")
                .name("Test User")
                .email("test@example.com")
                .build();

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longCpfRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldValidateEmailFormat() throws Exception {
        CustomerRequest invalidEmailRequest = CustomerRequest.builder()
                .cpf("12345678900")
                .name("Test User")
                .email("not-an-email")
                .build();

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEmailRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldValidateNameLength() throws Exception {
        String longName = "a".repeat(256);

        CustomerRequest longNameRequest = CustomerRequest.builder()
                .cpf("12345678900")
                .name(longName)
                .email("test@example.com")
                .build();

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longNameRequest)))
                .andExpect(status().isBadRequest());
    }
}