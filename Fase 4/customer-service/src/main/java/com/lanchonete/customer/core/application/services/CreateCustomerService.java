package com.lanchonete.customer.core.application.services;

import org.springframework.stereotype.Service;

import com.lanchonete.customer.core.application.dto.CustomerDTO;
import com.lanchonete.customer.core.application.usecases.CreateCustomerUseCase;
import com.lanchonete.customer.core.application.usecases.FindCustomerUseCase;
import com.lanchonete.customer.core.domain.exceptions.CustomerAlreadyExistsException;
import com.lanchonete.customer.core.domain.model.Customer;
import com.lanchonete.customer.core.domain.repositories.CustomerRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CreateCustomerService implements CreateCustomerUseCase{

    private final CustomerRepository customerRepository;
    private final FindCustomerUseCase findCustomerUseCase;

    @Override
    @Transactional
    public Customer createCustomer(final CustomerDTO customerDTO) {
        log.info("Creating customer with CPF: {}", customerDTO.getCpf());
        
        if (findCustomerUseCase.getCustomerOptionalByCpf(customerDTO.getCpf()).isPresent()) {
            throw new CustomerAlreadyExistsException("Customer with CPF " + customerDTO.getCpf() + " already exists");
        }
  
        return customerRepository.saveCustomer(customerDTO.toCustomer()); 
    }
    
}


