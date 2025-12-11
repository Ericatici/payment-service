package com.lanchonete.customer.core.application.dto;

import com.lanchonete.customer.core.domain.model.Customer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDTO {
    private String cpf;
    private String name;
    private String email; 

    public Customer toCustomer() { 
        
        return Customer.builder()
                .cpf(this.cpf)
                .name(this.name)
                .email(this.email)
                .build();
    }
}


