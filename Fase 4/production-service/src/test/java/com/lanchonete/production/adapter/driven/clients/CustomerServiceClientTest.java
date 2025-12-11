package com.lanchonete.production.adapter.driven.clients;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.lanchonete.production.core.application.dto.CustomerDTO;

@ExtendWith(MockitoExtension.class)
class CustomerServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CustomerServiceClient customerServiceClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(customerServiceClient, "customerServiceUrl", "http://localhost:8081");
    }

    @Test
    void shouldGetCustomerByCpfSuccessfully() {
        String cpf = "12345678900";
        CustomerDTO expectedCustomer = CustomerDTO.builder()
                .cpf(cpf)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        when(restTemplate.getForObject(anyString(), eq(CustomerDTO.class)))
                .thenReturn(expectedCustomer);

        CustomerDTO result = customerServiceClient.getCustomerByCpf(cpf);

        assertNotNull(result);
        assertEquals(cpf, result.getCpf());
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(CustomerDTO.class));
    }

    @Test
    void shouldReturnNullWhenCustomerServiceThrowsException() {
        String cpf = "99999999999";

        when(restTemplate.getForObject(anyString(), eq(CustomerDTO.class)))
                .thenThrow(new RestClientException("Service unavailable"));

        CustomerDTO result = customerServiceClient.getCustomerByCpf(cpf);

        assertNull(result);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(CustomerDTO.class));
    }

    @Test
    void shouldReturnNullWhenCustomerNotFound() {
        String cpf = "00000000000";

        when(restTemplate.getForObject(anyString(), eq(CustomerDTO.class)))
                .thenReturn(null);

        CustomerDTO result = customerServiceClient.getCustomerByCpf(cpf);

        assertNull(result);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(CustomerDTO.class));
    }
}

