package com.lanchonete.order.adapter.driven.clients;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.lanchonete.order.core.application.dto.OrderDTO;
import com.lanchonete.order.core.domain.model.enums.OrderStatusEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProductionServiceClient {

    private final RestTemplate restTemplate;

    @Value("${production.service.url:http://localhost:8082}")
    private String productionServiceUrl;

    public ProductionServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OrderDTO createOrder(OrderDTO orderDTO, String requestTraceId) {
        try {
            log.info("Calling Production Service to create order");
            String url = productionServiceUrl + "/production/order";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("requestTraceId", requestTraceId);
            HttpEntity<OrderDTO> request = new HttpEntity<>(orderDTO, headers);
            
            ResponseEntity<OrderDTO> response = restTemplate.exchange(
                    url, 
                    HttpMethod.POST, 
                    request, 
                    OrderDTO.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            log.error("Error calling Production Service to create order: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create order in Production Service", e);
        }
    }

    public OrderDTO getOrderById(Long orderId, String requestTraceId) {
        try {
            log.info("Calling Production Service to get order with ID: {}", orderId);
            String url = productionServiceUrl + "/production/orders/" + orderId;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("requestTraceId", requestTraceId);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<OrderDTO> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    request, 
                    OrderDTO.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            log.error("Error calling Production Service for order {}: {}", orderId, e.getMessage());
            return null;
        }
    }

    public List<OrderDTO> getActiveOrders(String requestTraceId) {
        try {
            log.info("Calling Production Service to get active orders");
            String url = productionServiceUrl + "/production/orders/active";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("requestTraceId", requestTraceId);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<OrderDTO[]> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    request, 
                    OrderDTO[].class
            );
            
            OrderDTO[] orders = response.getBody();
            return orders != null ? Arrays.asList(orders) : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error calling Production Service for active orders: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<OrderDTO> getAllOrders(String requestTraceId) {
        try {
            log.info("Calling Production Service to get all orders");
            String url = productionServiceUrl + "/production/orders";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("requestTraceId", requestTraceId);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<OrderDTO[]> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    request, 
                    OrderDTO[].class
            );
            
            OrderDTO[] orders = response.getBody();
            return orders != null ? Arrays.asList(orders) : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error calling Production Service for all orders: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<OrderDTO> getOrdersByStatus(OrderStatusEnum status, String requestTraceId) {
        try {
            log.info("Calling Production Service to get orders by status: {}", status);
            String url = productionServiceUrl + "/production/orders/status/" + status;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("requestTraceId", requestTraceId);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<OrderDTO[]> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    request, 
                    OrderDTO[].class
            );
            
            OrderDTO[] orders = response.getBody();
            return orders != null ? Arrays.asList(orders) : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error calling Production Service for orders by status: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}



