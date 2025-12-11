package com.lanchonete.customer.mocks;

import com.lanchonete.customer.core.application.dto.CustomerDTO;

public class CustomerDTOMock {

    public static CustomerDTO createCustomerDTOMock() {
        return CustomerDTO.builder()
                .cpf("12345678900")
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
    }

    public static CustomerDTO createCustomerDTOMock(String cpf, String name, String email) {
        return CustomerDTO.builder()
                .cpf(cpf)
                .name(name)
                .email(email)
                .build();
    }
}



