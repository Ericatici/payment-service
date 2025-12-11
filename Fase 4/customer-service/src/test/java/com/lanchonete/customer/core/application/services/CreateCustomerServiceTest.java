package com.lanchonete.customer.core.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lanchonete.customer.core.application.dto.CustomerDTO;
import com.lanchonete.customer.core.application.usecases.FindCustomerUseCase;
import com.lanchonete.customer.core.domain.exceptions.CustomerAlreadyExistsException;
import com.lanchonete.customer.core.domain.model.Customer;
import com.lanchonete.customer.core.domain.repositories.CustomerRepository;
import com.lanchonete.customer.mocks.CustomerMock;

@ExtendWith(MockitoExtension.class)
class CreateCustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private FindCustomerUseCase findCustomerUseCase;

    @InjectMocks
    private CreateCustomerService createCustomerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = CustomerMock.createCustomerMock();
    }

    @Test
    void shouldCreateCustomerSuccessfully() {
        when(findCustomerUseCase.getCustomerOptionalByCpf(customer.getCpf())).thenReturn(java.util.Optional.empty());
        when(customerRepository.saveCustomer(any(Customer.class))).thenReturn(customer);

        CustomerDTO customerDTO = CustomerDTO.builder()
                .cpf(customer.getCpf())
                .name(customer.getName())
                .email(customer.getEmail())
                .build();
        Customer result = createCustomerService.createCustomer(customerDTO);

        assertNotNull(result);
        assertEquals(customer.getCpf(), result.getCpf());
        assertEquals(customer.getName(), result.getName());
        assertEquals(customer.getEmail(), result.getEmail());
        
        verify(findCustomerUseCase, times(1)).getCustomerOptionalByCpf(customer.getCpf());
        verify(customerRepository, times(1)).saveCustomer(any(Customer.class));
    }

    @Test
    void shouldThrowExceptionWhenCustomerAlreadyExists() {
        when(findCustomerUseCase.getCustomerOptionalByCpf(customer.getCpf())).thenReturn(java.util.Optional.of(customer));
        assertThrows(CustomerAlreadyExistsException.class, () -> {
            CustomerDTO customerDTO = CustomerDTO.builder()
                    .cpf(customer.getCpf())
                    .name(customer.getName())
                    .email(customer.getEmail())
                    .build();
            createCustomerService.createCustomer(customerDTO);
        });

        verify(findCustomerUseCase, times(1)).getCustomerOptionalByCpf(customer.getCpf());
        verify(customerRepository, never()).saveCustomer(any(Customer.class));
    }
}



