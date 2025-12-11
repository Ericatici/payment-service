package com.lanchonete.customer.mocks;

import com.lanchonete.customer.core.domain.model.Customer;

public class CustomerMock {

    public static Customer createCustomerMock() {
        return Customer.builder()
                .cpf("12345678900")
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
    }

    public static Customer createCustomerMock(String cpf, String name, String email) {
        return Customer.builder()
                .cpf(cpf)
                .name(name)
                .email(email)
                .build();
    }
}



