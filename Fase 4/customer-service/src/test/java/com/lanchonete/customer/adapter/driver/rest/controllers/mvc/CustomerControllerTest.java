package com.lanchonete.customer.adapter.driver.rest.controllers.mvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
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
import com.lanchonete.customer.core.domain.model.Customer;
import com.lanchonete.customer.mocks.CustomerMock;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

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

    private Customer customer;
    private CustomerRequest customerRequest;

    @BeforeEach
    void setUp() {
        customer = CustomerMock.createCustomerMock();
        customerRequest = new CustomerRequest();
        customerRequest.setCpf("12345678900");
        customerRequest.setName("John Doe");
        customerRequest.setEmail("john.doe@example.com");
    }

    @Test
    void shouldCreateCustomerSuccessfully() throws Exception {
        when(createCustomerUseCase.createCustomer(any())).thenReturn(customer);

        mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf").value(customer.getCpf()))
                .andExpect(jsonPath("$.name").value(customer.getName()))
                .andExpect(jsonPath("$.email").value(customer.getEmail()));

        verify(createCustomerUseCase, times(1)).createCustomer(any());
    }

    @Test
    void shouldFindCustomerByCpfSuccessfully() throws Exception {
        when(findCustomerUseCase.getCustomerOptionalByCpf(customer.getCpf())).thenReturn(java.util.Optional.of(customer));

        mockMvc.perform(get("/customer/{cpf}", customer.getCpf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value(customer.getCpf()))
                .andExpect(jsonPath("$.name").value(customer.getName()));

        verify(findCustomerUseCase, times(1)).getCustomerOptionalByCpf(customer.getCpf());
    }

    @Test
    void shouldReturn404WhenCustomerNotFound() throws Exception {
        String cpf = "99999999999";
        when(findCustomerUseCase.getCustomerOptionalByCpf(cpf))
                .thenReturn(java.util.Optional.empty());
        mockMvc.perform(get("/customer/{cpf}", cpf))
                .andExpect(status().isNotFound());

        verify(findCustomerUseCase, times(1)).getCustomerOptionalByCpf(cpf);
    }
}



