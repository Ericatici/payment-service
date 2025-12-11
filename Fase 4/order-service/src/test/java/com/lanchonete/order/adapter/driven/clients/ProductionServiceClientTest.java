package com.lanchonete.order.adapter.driven.clients;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.lanchonete.order.core.application.dto.OrderDTO;
import com.lanchonete.order.core.domain.model.enums.OrderStatusEnum;

@ExtendWith(MockitoExtension.class)
class ProductionServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProductionServiceClient productionServiceClient;

    private String productionServiceUrl = "http://localhost:8082";
    private OrderDTO orderDTO;
    private String requestTraceId = "test-trace-id";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(productionServiceClient, "productionServiceUrl", productionServiceUrl);
        
        orderDTO = OrderDTO.builder()
                .id(1L)
                .customerCpf("12345678900")
                .totalPrice(new BigDecimal("75.00"))
                .status(OrderStatusEnum.RECEIVED)
                .build();
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        ResponseEntity<OrderDTO> responseEntity = new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
        when(restTemplate.exchange(
                eq(productionServiceUrl + "/production/order"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(OrderDTO.class)))
                .thenReturn(responseEntity);

        OrderDTO result = productionServiceClient.createOrder(orderDTO, requestTraceId);

        assertNotNull(result);
        assertEquals(orderDTO.getId(), result.getId());
        assertEquals(orderDTO.getCustomerCpf(), result.getCustomerCpf());
        
        verify(restTemplate, times(1)).exchange(
                eq(productionServiceUrl + "/production/order"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(OrderDTO.class));
    }

    @Test
    void shouldThrowExceptionWhenCreateOrderFails() {
        when(restTemplate.exchange(
                eq(productionServiceUrl + "/production/order"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(OrderDTO.class)))
                .thenThrow(new RestClientException("Connection failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productionServiceClient.createOrder(orderDTO, requestTraceId);
        });
        
        assertTrue(exception.getMessage().contains("Failed to create order in Production Service"));
        verify(restTemplate, times(1)).exchange(
                eq(productionServiceUrl + "/production/order"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(OrderDTO.class));
    }

    @Test
    void shouldGetOrderByIdSuccessfully() {
        Long orderId = 1L;
        ResponseEntity<OrderDTO> responseEntity = new ResponseEntity<>(orderDTO, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(productionServiceUrl + "/production/orders/" + orderId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(OrderDTO.class)))
                .thenReturn(responseEntity);

        OrderDTO result = productionServiceClient.getOrderById(orderId, requestTraceId);

        assertNotNull(result);
        assertEquals(orderDTO.getId(), result.getId());
        
        verify(restTemplate, times(1)).exchange(
                eq(productionServiceUrl + "/production/orders/" + orderId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(OrderDTO.class));
    }

    @Test
    void shouldReturnNullWhenGetOrderByIdFails() {
        Long orderId = 999L;
        when(restTemplate.exchange(
                eq(productionServiceUrl + "/production/orders/" + orderId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(OrderDTO.class)))
                .thenThrow(new RestClientException("Order not found"));

        OrderDTO result = productionServiceClient.getOrderById(orderId, requestTraceId);

        assertNull(result);
        
        verify(restTemplate, times(1)).exchange(
                eq(productionServiceUrl + "/production/orders/" + orderId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(OrderDTO.class));
    }

    @Test
    void shouldGetActiveOrdersSuccessfully() {
        OrderDTO[] orderArray = {orderDTO};
        ResponseEntity<OrderDTO[]> responseEntity = new ResponseEntity<>(orderArray, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(productionServiceUrl + "/production/orders/active"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(OrderDTO[].class)))
                .thenReturn(responseEntity);

        List<OrderDTO> result = productionServiceClient.getActiveOrders(requestTraceId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(orderDTO.getId(), result.get(0).getId());
        
        verify(restTemplate, times(1)).exchange(
                eq(productionServiceUrl + "/production/orders/active"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(OrderDTO[].class));
    }

    @Test
    void shouldReturnEmptyListWhenGetActiveOrdersFails() {
        when(restTemplate.exchange(
                eq(productionServiceUrl + "/production/orders/active"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(OrderDTO[].class)))
                .thenThrow(new RestClientException("Service unavailable"));

        List<OrderDTO> result = productionServiceClient.getActiveOrders(requestTraceId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(restTemplate, times(1)).exchange(
                eq(productionServiceUrl + "/production/orders/active"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(OrderDTO[].class));
    }

    @Test
    void shouldReturnEmptyListWhenGetActiveOrdersReturnsNull() {
        ResponseEntity<OrderDTO[]> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(productionServiceUrl + "/production/orders/active"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(OrderDTO[].class)))
                .thenReturn(responseEntity);

        List<OrderDTO> result = productionServiceClient.getActiveOrders(requestTraceId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldGetAllOrdersSuccessfully() {
        OrderDTO[] orderArray = {orderDTO};
        ResponseEntity<OrderDTO[]> responseEntity = new ResponseEntity<>(orderArray, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(productionServiceUrl + "/production/orders"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(OrderDTO[].class)))
                .thenReturn(responseEntity);

        List<OrderDTO> result = productionServiceClient.getAllOrders(requestTraceId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        
        verify(restTemplate, times(1)).exchange(
                eq(productionServiceUrl + "/production/orders"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(OrderDTO[].class));
    }

    @Test
    void shouldReturnEmptyListWhenGetAllOrdersFails() {
        when(restTemplate.exchange(
                eq(productionServiceUrl + "/production/orders"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(OrderDTO[].class)))
                .thenThrow(new RestClientException("Service unavailable"));

        List<OrderDTO> result = productionServiceClient.getAllOrders(requestTraceId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldGetOrdersByStatusSuccessfully() {
        OrderDTO[] orderArray = {orderDTO};
        ResponseEntity<OrderDTO[]> responseEntity = new ResponseEntity<>(orderArray, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(productionServiceUrl + "/production/orders/status/RECEIVED"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(OrderDTO[].class)))
                .thenReturn(responseEntity);

        List<OrderDTO> result = productionServiceClient.getOrdersByStatus(OrderStatusEnum.RECEIVED, requestTraceId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        
        verify(restTemplate, times(1)).exchange(
                eq(productionServiceUrl + "/production/orders/status/RECEIVED"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(OrderDTO[].class));
    }

    @Test
    void shouldReturnEmptyListWhenGetOrdersByStatusFails() {
        when(restTemplate.exchange(
                eq(productionServiceUrl + "/production/orders/status/IN_PREPARATION"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(OrderDTO[].class)))
                .thenThrow(new RestClientException("Service unavailable"));

        List<OrderDTO> result = productionServiceClient.getOrdersByStatus(OrderStatusEnum.IN_PREPARATION, requestTraceId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSetRequestTraceIdInHeaders() {
        ResponseEntity<OrderDTO> responseEntity = new ResponseEntity<>(orderDTO, HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(OrderDTO.class)))
                .thenAnswer(invocation -> {
                    HttpEntity<?> entity = invocation.getArgument(2);
                    HttpHeaders headers = entity.getHeaders();
                    assertTrue(headers.containsKey("requestTraceId"));
                    assertEquals(requestTraceId, headers.getFirst("requestTraceId"));
                    return responseEntity;
                });

        productionServiceClient.getOrderById(1L, requestTraceId);

        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(OrderDTO.class));
    }
}

