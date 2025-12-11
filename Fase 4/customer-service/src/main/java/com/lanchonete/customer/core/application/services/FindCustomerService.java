package com.lanchonete.customer.core.application.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lanchonete.customer.core.application.usecases.FindCustomerUseCase;
import com.lanchonete.customer.core.domain.exceptions.CustomerNotFoundException;
import com.lanchonete.customer.core.domain.model.Customer;
import com.lanchonete.customer.core.domain.repositories.CustomerRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class FindCustomerService implements FindCustomerUseCase {
    
    private final CustomerRepository customerRepository;
    
    @Override
    public Optional<Customer> getCustomerOptionalByCpf(final String cpf) {
        log.info("Finding customer with CPF: {}", cpf);

        return customerRepository.findById(cpf);
    }

    @Override
    public Customer getCustomerByCpf(final String cpf) {
        log.info("Finding customer with CPF: {}", cpf);

        Optional<Customer> customerOptional = Optional.ofNullable(customerRepository.findById(cpf)
        .orElseThrow(() -> new CustomerNotFoundException("Customer with CPF " + cpf + " not found")));

        return customerOptional.get();
    }
    
}


