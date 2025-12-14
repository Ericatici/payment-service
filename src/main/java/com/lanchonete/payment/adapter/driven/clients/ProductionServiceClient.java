package com.lanchonete.payment.adapter.driven.clients;

import com.lanchonete.payment.core.application.dto.OrderDTO;
import com.lanchonete.payment.core.domain.model.PaymentConfirmation;
import com.lanchonete.payment.core.domain.repositories.OrderRepository;
import com.lanchonete.payment.core.domain.repositories.OrderRepository.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductionServiceClient implements OrderRepository {

    private final RestTemplate restTemplate;
    private final String productionServiceUrl;

    public ProductionServiceClient(RestTemplate restTemplate, @Value("${production.service.url}") String productionServiceUrl) {
        this.restTemplate = restTemplate;
        this.productionServiceUrl = productionServiceUrl;
    }

    @Override
    public Order getOrderById(Long orderId) {
        OrderDTO orderDTO = restTemplate.getForObject(productionServiceUrl + "/production/orders/" + orderId, OrderDTO.class);
        return toOrder(orderDTO);
    }

    @Override
    public Order getOrderByPaymentId(String paymentId) {
        OrderDTO orderDTO = restTemplate.getForObject(productionServiceUrl + "/production/orders/payment/" + paymentId, OrderDTO.class);
        return toOrder(orderDTO);
    }

    @Override
    public void updateOrderPaymentStatus(Long orderId, PaymentConfirmation paymentConfirmation) {
        restTemplate.exchange(
            productionServiceUrl + "/production/orders/" + orderId + "/payment-status",
            HttpMethod.PUT,
            new HttpEntity<>(paymentConfirmation),
            Void.class
        );
    }

    private Order toOrder(OrderDTO orderDTO) {
        if (orderDTO == null) {
            return null;
        }
        return new Order(
            orderDTO.getId(),
            orderDTO.getCustomerCpf(),
            orderDTO.getPaymentId(),
            orderDTO.getPaymentStatus()
        );
    }
}
