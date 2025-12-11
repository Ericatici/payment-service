package com.lanchonete.production.core.application.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CustomerDTOTest {

    @Test
    void shouldCreateCustomerDTOWithBuilder() {
        CustomerDTO customer = CustomerDTO.builder()
                .cpf("12345678900")
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        assertNotNull(customer);
        assertEquals("12345678900", customer.getCpf());
        assertEquals("John Doe", customer.getName());
        assertEquals("john.doe@example.com", customer.getEmail());
    }

    @Test
    void shouldCreateCustomerDTOWithNoArgsConstructor() {
        CustomerDTO customer = new CustomerDTO();

        assertNotNull(customer);
        assertNull(customer.getCpf());
        assertNull(customer.getName());
        assertNull(customer.getEmail());
    }

    @Test
    void shouldCreateCustomerDTOWithAllArgsConstructor() {
        CustomerDTO customer = new CustomerDTO("12345678900", "John Doe", "john.doe@example.com");

        assertNotNull(customer);
        assertEquals("12345678900", customer.getCpf());
        assertEquals("John Doe", customer.getName());
        assertEquals("john.doe@example.com", customer.getEmail());
    }

    @Test
    void shouldSetAndGetFields() {
        CustomerDTO customer = new CustomerDTO();
        customer.setCpf("12345678900");
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");

        assertEquals("12345678900", customer.getCpf());
        assertEquals("John Doe", customer.getName());
        assertEquals("john.doe@example.com", customer.getEmail());
    }
}

