package com.lanchonete.production.adapter.driven.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.lanchonete.production.core.application.dto.PaymentDataDTO;
import com.lanchonete.production.core.application.ports.PaymentServicePort;
import com.lanchonete.production.core.domain.model.Order;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PaymentServiceClient implements PaymentServicePort {

    @Value("${payment.service.url:http://localhost:8084}")
    private String paymentServiceUrl;

    private final RestTemplate restTemplate;

    public PaymentServiceClient(final RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @Override
    public PaymentDataDTO getPaymentData(final Order order, String requestTraceId) {
         try {
            log.info("Calling Production Service to create order");
            String url = paymentServiceUrl + "/paymentData";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("requestTraceId", requestTraceId);
            HttpEntity<Order> request = new HttpEntity<>(order, headers);
            
            ResponseEntity<PaymentDataDTO> response = restTemplate.exchange(
                    url, 
                    HttpMethod.POST, 
                    request, 
                    PaymentDataDTO.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            log.error("Error calling Payment Service to get payment data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get payment data from Payment Service", e);
        }
    }
    
}
