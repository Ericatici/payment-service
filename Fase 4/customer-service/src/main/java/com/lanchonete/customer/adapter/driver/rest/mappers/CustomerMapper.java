package com.lanchonete.customer.adapter.driver.rest.mappers;

import com.lanchonete.customer.adapter.driver.rest.responses.CustomerResponse;
import com.lanchonete.customer.core.domain.model.Customer;

public class CustomerMapper {

    private CustomerMapper() {
    }

    public static CustomerResponse toCustomerResponse(Customer customer) {
        if (customer == null) {
            return null;
        }
        return CustomerResponse.builder()
                .cpf(customer.getCpf())
                .name(customer.getName())
                .email(customer.getEmail())
                .build();
    }
}

