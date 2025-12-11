package com.lanchonete.customer.core.domain.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    private String cpf;
    private String name;
    private String email;
    private Instant createdDate;
    private Instant updatedDate;
    
}


