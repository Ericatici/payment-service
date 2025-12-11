package com.lanchonete.production.adapter.driven.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.lanchonete.production.core.application.dto.CustomerDTO;
import com.lanchonete.production.core.application.ports.CustomerServicePort;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class CustomerServiceClient implements CustomerServicePort {

    @Value("${customer.service.url:http://localhost:8081}")
    private String customerServiceUrl;

    private final RestTemplate restTemplate;

    public CustomerServiceClient(final RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }
    
    @Override
    public CustomerDTO getCustomerByCpf(String cpf) {
        try {
            log.info("Calling Customer Service to get customer with CPF: {}", cpf);
            String url = customerServiceUrl + "/customer/" + cpf;
            return restTemplate.getForObject(url, CustomerDTO.class);
        } catch (Exception e) {
            log.error("Error calling Customer Service for CPF {}: {}", cpf, e.getMessage());
            return null;
        }
    }
}

