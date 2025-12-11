package com.lanchonete.customer.core.application.usecases;

import java.util.Optional;

import com.lanchonete.customer.core.domain.model.Customer;

public interface FindCustomerUseCase {
    Optional<Customer> getCustomerOptionalByCpf(String cpf);
    Customer getCustomerByCpf(String cpf);
}


