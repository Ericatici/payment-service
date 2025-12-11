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
public class CustomerUpdateRequest {    
    
    @NotBlank(message = "Name is required")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255)
    private String email;

    public CustomerDTO toDto(String cpf) { 
        return CustomerDTO.builder()
                .cpf(cpf)
                .name(this.name)
                .email(this.email)
                .build();
    }
}