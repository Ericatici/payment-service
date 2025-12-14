package com.lanchonete.payment.adapter.driven.clients;

import com.lanchonete.payment.core.application.dto.OrderDTO;
import com.lanchonete.payment.core.domain.model.PaymentConfirmation;
import com.lanchonete.payment.core.domain.model.enums.PaymentStatusEnum;
import com.lanchonete.payment.core.domain.repositories.OrderRepository.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductionServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ProductionServiceClient productionServiceClient;

    private static final String PRODUCTION_SERVICE_URL = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        productionServiceClient = new ProductionServiceClient(restTemplate, PRODUCTION_SERVICE_URL);
    }

    @Test
    void shouldGetOrderById() {
        Long orderId = 1L;
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(orderId);
        orderDTO.setCustomerCpf("12345678900");
        orderDTO.setPaymentId("payment-123");
        orderDTO.setPaymentStatus(PaymentStatusEnum.PENDING);

        when(restTemplate.getForObject(anyString(), eq(OrderDTO.class)))
                .thenReturn(orderDTO);

        Order result = productionServiceClient.getOrderById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals("12345678900", result.getCustomerCpf());
        assertEquals("payment-123", result.getPaymentId());
        assertEquals(PaymentStatusEnum.PENDING, result.getPaymentStatus());
        verify(restTemplate).getForObject(PRODUCTION_SERVICE_URL + "/production/orders/" + orderId, OrderDTO.class);
    }

    @Test
    void shouldReturnNullWhenOrderDTOIsNull() {
        Long orderId = 1L;

        when(restTemplate.getForObject(anyString(), eq(OrderDTO.class)))
                .thenReturn(null);

        Order result = productionServiceClient.getOrderById(orderId);

        assertNull(result);
        verify(restTemplate).getForObject(PRODUCTION_SERVICE_URL + "/production/orders/" + orderId, OrderDTO.class);
    }

    @Test
    void shouldGetOrderByPaymentId() {
        String paymentId = "payment-123";
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setCustomerCpf("12345678900");
        orderDTO.setPaymentId(paymentId);
        orderDTO.setPaymentStatus(PaymentStatusEnum.APPROVED);

        when(restTemplate.getForObject(anyString(), eq(OrderDTO.class)))
                .thenReturn(orderDTO);

        Order result = productionServiceClient.getOrderByPaymentId(paymentId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("12345678900", result.getCustomerCpf());
        assertEquals(paymentId, result.getPaymentId());
        assertEquals(PaymentStatusEnum.APPROVED, result.getPaymentStatus());
        verify(restTemplate).getForObject(PRODUCTION_SERVICE_URL + "/production/orders/payment/" + paymentId, OrderDTO.class);
    }

    @Test
    void shouldReturnNullWhenOrderByPaymentIdIsNull() {
        String paymentId = "payment-123";

        when(restTemplate.getForObject(anyString(), eq(OrderDTO.class)))
                .thenReturn(null);

        Order result = productionServiceClient.getOrderByPaymentId(paymentId);

        assertNull(result);
        verify(restTemplate).getForObject(PRODUCTION_SERVICE_URL + "/production/orders/payment/" + paymentId, OrderDTO.class);
    }

    @Test
    void shouldUpdateOrderPaymentStatus() {
        Long orderId = 1L;
        PaymentConfirmation paymentConfirmation = PaymentConfirmation.builder()
                .id("payment-123")
                .status("processed")
                .totalAmount(50.0)
                .build();

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(null);

        productionServiceClient.updateOrderPaymentStatus(orderId, paymentConfirmation);

        verify(restTemplate).exchange(
                eq(PRODUCTION_SERVICE_URL + "/production/orders/" + orderId + "/payment-status"),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(Void.class)
        );
    }
}
