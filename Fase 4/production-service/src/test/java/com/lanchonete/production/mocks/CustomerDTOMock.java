package com.lanchonete.production.mocks;

import com.lanchonete.production.core.application.dto.CustomerDTO;

public class CustomerDTOMock {

    public static CustomerDTO createCustomerDTOMock() {
        return CustomerDTO.builder()
                .cpf("12345678900")
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
    }
}

