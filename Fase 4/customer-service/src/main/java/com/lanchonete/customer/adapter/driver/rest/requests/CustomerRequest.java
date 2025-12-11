package com.lanchonete.customer.adapter.driver.rest.requests;

import com.lanchonete.customer.core.application.dto.CustomerDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

    @NotBlank(message = "CPF is required")
    @Size(min = 11, max = 11, message = "CPF must have 11 digits")
    private String cpf;
    
    @NotBlank(message = "Name is required")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255)
    private String email;


    public CustomerDTO toDto() { 
        return CustomerDTO.builder()
                .cpf(this.cpf)
                .name(this.name)
                .email(this.email)
                .build();
    }
    
}