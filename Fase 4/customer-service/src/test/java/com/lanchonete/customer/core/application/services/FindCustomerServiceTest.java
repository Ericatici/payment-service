package com.lanchonete.customer.core.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lanchonete.customer.core.application.usecases.FindCustomerUseCase;
import com.lanchonete.customer.core.domain.exceptions.CustomerNotFoundException;
import com.lanchonete.customer.core.domain.model.Customer;
import com.lanchonete.customer.core.domain.repositories.CustomerRepository;
import com.lanchonete.customer.mocks.CustomerMock;

@ExtendWith(MockitoExtension.class)
class FindCustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private FindCustomerUseCase findCustomerUseCase;

    @InjectMocks
    private FindCustomerService findCustomerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = CustomerMock.createCustomerMock();
    }

    @Test
    void shouldFindCustomerByCpfSuccessfully() {
        when(customerRepository.findById(customer.getCpf())).thenReturn(java.util.Optional.of(customer));

        Customer result = findCustomerService.getCustomerByCpf(customer.getCpf());

        assertNotNull(result);
        assertEquals(customer.getCpf(), result.getCpf());
        assertEquals(customer.getName(), result.getName());
        
        verify(customerRepository, times(1)).findById(customer.getCpf());
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        String cpf = "99999999999";
        when(customerRepository.findById(cpf)).thenReturn(java.util.Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> {
            findCustomerService.getCustomerByCpf(cpf);
        });

        verify(customerRepository, times(1)).findById(cpf);
    }

    @Test
    void shouldGetCustomerOptionalByCpfSuccessfully() {
        when(customerRepository.findById(customer.getCpf())).thenReturn(java.util.Optional.of(customer));

        java.util.Optional<Customer> result = findCustomerService.getCustomerOptionalByCpf(customer.getCpf());

        assertTrue(result.isPresent());
        assertEquals(customer.getCpf(), result.get().getCpf());
        assertEquals(customer.getName(), result.get().getName());
        
        verify(customerRepository, times(1)).findById(customer.getCpf());
    }

    @Test
    void shouldReturnEmptyOptionalWhenCustomerNotFound() {
        String cpf = "99999999999";
        when(customerRepository.findById(cpf)).thenReturn(java.util.Optional.empty());

        java.util.Optional<Customer> result = findCustomerService.getCustomerOptionalByCpf(cpf);

        assertTrue(result.isEmpty());
        
        verify(customerRepository, times(1)).findById(cpf);
    }
}



