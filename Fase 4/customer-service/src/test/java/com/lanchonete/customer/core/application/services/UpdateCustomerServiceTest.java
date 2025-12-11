package com.lanchonete.customer.core.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lanchonete.customer.core.application.dto.CustomerDTO;
import com.lanchonete.customer.core.domain.exceptions.CustomerNotFoundException;
import com.lanchonete.customer.core.domain.model.Customer;
import com.lanchonete.customer.core.domain.repositories.CustomerRepository;
import com.lanchonete.customer.mocks.CustomerDTOMock;
import com.lanchonete.customer.mocks.CustomerMock;

@ExtendWith(MockitoExtension.class)
class UpdateCustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private UpdateCustomerService updateCustomerService;

    private Customer existingCustomer;

    @BeforeEach
    void setUp() {
        existingCustomer = CustomerMock.createCustomerMock();
    }

    @Test
    void shouldUpdateCustomerSuccessfully() {
        CustomerDTO updatedDTO = CustomerDTOMock.createCustomerDTOMock(
            existingCustomer.getCpf(),
            "Updated Name",
            "updated@example.com"
        );
        
        Customer updatedCustomer = Customer.builder()
                .cpf(updatedDTO.getCpf())
                .name(updatedDTO.getName())
                .email(updatedDTO.getEmail())
                .build();

        when(customerRepository.findById(existingCustomer.getCpf())).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.updateCustomer(any(Customer.class))).thenReturn(updatedCustomer);

        Customer result = updateCustomerService.updateCustomer(updatedDTO);

        assertNotNull(result);
        assertEquals(updatedDTO.getCpf(), result.getCpf());
        assertEquals(updatedDTO.getName(), result.getName());
        assertEquals(updatedDTO.getEmail(), result.getEmail());
        
        verify(customerRepository, times(1)).findById(existingCustomer.getCpf());
        verify(customerRepository, times(1)).updateCustomer(any(Customer.class));
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        String cpf = "99999999999";
        CustomerDTO dto = CustomerDTOMock.createCustomerDTOMock(cpf, "Test Name", "test@example.com");
        when(customerRepository.findById(cpf)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> {
            updateCustomerService.updateCustomer(dto);
        });

        verify(customerRepository, times(1)).findById(cpf);
        verify(customerRepository, never()).updateCustomer(any(Customer.class));
    }
}

