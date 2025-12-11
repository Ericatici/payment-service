package com.lanchonete.order.adapter.driver.rest.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lanchonete.order.adapter.driver.rest.requests.OrderRequest;
import com.lanchonete.order.adapter.driver.rest.responses.OrderResponse;
import com.lanchonete.order.core.application.usecases.CreateOrderUseCase;
import com.lanchonete.order.core.application.usecases.FindOrderUseCase;
import com.lanchonete.order.core.domain.model.Order;
import com.lanchonete.order.mocks.OrderMock;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private CreateOrderUseCase createOrderUseCase;

    @Mock
    private FindOrderUseCase findOrderUseCase;

    @InjectMocks
    private OrderController orderController;

    private Order order;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        order = OrderMock.createOrderMock();
        orderRequest = OrderRequest.builder()
                .customerCpf("12345678900")
                .items(Arrays.asList(
                        OrderRequest.OrderItemRequest.builder()
                                .productId("PROD-001")
                                .quantity(2)
                                .build()
                ))
                .build();
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        when(createOrderUseCase.create(any(Order.class))).thenReturn(order);

        ResponseEntity<OrderResponse> response = orderController.createOrder(
                "trace-123", 
                orderRequest
        );

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(order.getId(), response.getBody().getId());
        assertEquals(order.getCustomerCpf(), response.getBody().getCustomerCpf());
        
        verify(createOrderUseCase, times(1)).create(any(Order.class));
    }

    @Test
    void shouldGetOrderByIdSuccessfully() {
        when(findOrderUseCase.findById(order.getId())).thenReturn(order);

        ResponseEntity<OrderResponse> response = orderController.getOrderById(
                "trace-123", 
                order.getId()
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(order.getId(), response.getBody().getId());
        
        verify(findOrderUseCase, times(1)).findById(order.getId());
    }

    @Test
    void shouldGetActiveOrdersSuccessfully() {
        List<Order> orders = Arrays.asList(order);
        when(findOrderUseCase.findActiveOrders()).thenReturn(orders);

        ResponseEntity<List<OrderResponse>> response = orderController.getActiveOrders("trace-123");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        
        verify(findOrderUseCase, times(1)).findActiveOrders();
    }

    @Test
    void shouldGetAllOrdersSuccessfully() {
        List<Order> orders = Arrays.asList(order);
        when(findOrderUseCase.findAll()).thenReturn(orders);

        ResponseEntity<List<OrderResponse>> response = orderController.getAllOrders("trace-123");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        
        verify(findOrderUseCase, times(1)).findAll();
    }
}


