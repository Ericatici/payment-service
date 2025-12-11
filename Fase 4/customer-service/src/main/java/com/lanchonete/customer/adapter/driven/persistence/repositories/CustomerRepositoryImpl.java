package com.lanchonete.customer.adapter.driven.persistence.repositories;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.lanchonete.customer.adapter.driven.persistence.entities.CustomerEntity;
import com.lanchonete.customer.adapter.driven.persistence.repositories.jpa.JpaCustomerRepository;
import com.lanchonete.customer.core.domain.model.Customer;
import com.lanchonete.customer.core.domain.repositories.CustomerRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final JpaCustomerRepository jpaCustomerRepository;

    @Override
    public Optional<Customer> findById(final String cpf) {
        Optional<CustomerEntity> customerOptional = jpaCustomerRepository.findById(cpf);

        return customerOptional.map(CustomerEntity::toCustomer);
    }

    @Override
    public Customer saveCustomer(final Customer customer) {
        final CustomerEntity customerEntity = CustomerEntity.fromCustomer(customer);

        final CustomerEntity createdCustomer = jpaCustomerRepository.save(customerEntity); 

        return createdCustomer.toCustomer();
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        final CustomerEntity customerEntity = CustomerEntity.fromCustomer(customer);

        final CustomerEntity createdCustomer = jpaCustomerRepository.save(customerEntity); 

        return createdCustomer.toCustomer();
    }
    
}


