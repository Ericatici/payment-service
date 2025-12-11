package com.lanchonete.customer.core.domain.repositories;

import java.util.Optional;

import com.lanchonete.customer.core.domain.model.Customer;

public interface CustomerRepository {

    Optional<Customer> findById(String cpf);
    Customer saveCustomer(Customer customer);
    Customer updateCustomer(Customer customer);
    
}


