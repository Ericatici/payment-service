package com.lanchonete.customer.adapter.driven.persistence.repositories;

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

import com.lanchonete.customer.adapter.driven.persistence.entities.CustomerEntity;
import com.lanchonete.customer.adapter.driven.persistence.repositories.jpa.JpaCustomerRepository;
import com.lanchonete.customer.core.domain.model.Customer;
import com.lanchonete.customer.mocks.CustomerMock;

@ExtendWith(MockitoExtension.class)
class CustomerRepositoryImplTest {

    @Mock
    private JpaCustomerRepository jpaCustomerRepository;

    @InjectMocks
    private CustomerRepositoryImpl customerRepository;

    private Customer customer;
    private CustomerEntity customerEntity;

    @BeforeEach
    void setUp() {
        customer = CustomerMock.createCustomerMock();
        
        customerEntity = new CustomerEntity();
        customerEntity.setCpf(customer.getCpf());
        customerEntity.setName(customer.getName());
        customerEntity.setEmail(customer.getEmail());
    }

    @Test
    void shouldSaveCustomerSuccessfully() {
        when(jpaCustomerRepository.save(any(CustomerEntity.class))).thenReturn(customerEntity);

        Customer result = customerRepository.saveCustomer(customer);

        assertNotNull(result);
        assertEquals(customer.getCpf(), result.getCpf());
        assertEquals(customer.getName(), result.getName());
        
        verify(jpaCustomerRepository, times(1)).save(any(CustomerEntity.class));
    }

    @Test
    void shouldFindCustomerByCpfSuccessfully() {
        when(jpaCustomerRepository.findById(customer.getCpf())).thenReturn(Optional.of(customerEntity));

        Customer result = customerRepository.findById(customer.getCpf()).orElse(null);

        assertNotNull(result);
        assertEquals(customer.getCpf(), result.getCpf());
        
        verify(jpaCustomerRepository, times(1)).findById(customer.getCpf());
    }

    @Test
    void shouldReturnNullWhenCustomerNotFound() {
        String cpf = "99999999999";
        when(jpaCustomerRepository.findById(cpf)).thenReturn(Optional.empty());

        Customer result = customerRepository.findById(cpf).orElse(null);

        assertNull(result);
        
        verify(jpaCustomerRepository, times(1)).findById(cpf);
    }

    @Test
    void shouldUpdateCustomerSuccessfully() {
        Customer updatedCustomer = CustomerMock.createCustomerMock("12345678900", "Updated Name", "updated@example.com");
        CustomerEntity updatedEntity = new CustomerEntity();
        updatedEntity.setCpf(updatedCustomer.getCpf());
        updatedEntity.setName(updatedCustomer.getName());
        updatedEntity.setEmail(updatedCustomer.getEmail());
        
        when(jpaCustomerRepository.save(any(CustomerEntity.class))).thenReturn(updatedEntity);

        Customer result = customerRepository.updateCustomer(updatedCustomer);

        assertNotNull(result);
        assertEquals(updatedCustomer.getCpf(), result.getCpf());
        assertEquals(updatedCustomer.getName(), result.getName());
        assertEquals(updatedCustomer.getEmail(), result.getEmail());
        
        verify(jpaCustomerRepository, times(1)).save(any(CustomerEntity.class));
    }
}



