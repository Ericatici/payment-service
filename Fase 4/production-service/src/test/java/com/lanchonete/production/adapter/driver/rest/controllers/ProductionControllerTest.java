package com.lanchonete.production.adapter.driver.rest.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

import com.lanchonete.production.adapter.driver.rest.responses.OrderResponse;
import com.lanchonete.production.core.application.usecases.FindOrderUseCase;
import com.lanchonete.production.core.application.usecases.UpdateOrderUseCase;
import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.production.mocks.OrderMock;

@ExtendWith(MockitoExtension.class)
class ProductionControllerTest {

    @Mock
    private FindOrderUseCase findOrderUseCase;

    @Mock
    private UpdateOrderUseCase updateOrderUseCase;

    @InjectMocks
    private ProductionController productionController;

    private Order order;

    @BeforeEach
    void setUp() {
        order = OrderMock.createOrderMock();
    }

    @Test
    void shouldGetAllOrdersSuccessfully() {
        List<Order> orders = Arrays.asList(order);
        when(findOrderUseCase.findAllOrders()).thenReturn(orders);
        ResponseEntity<List<OrderResponse>> response = productionController.getAllOrders("trace-123");
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        
        verify(findOrderUseCase, times(1)).findAllOrders();
    }

    @Test
    void shouldGetOrdersByStatusSuccessfully() {
        List<Order> orders = Arrays.asList(order);
        when(findOrderUseCase.findOrdersByStatus(any(OrderStatusEnum.class))).thenReturn(orders);
        ResponseEntity<List<OrderResponse>> response = productionController.getOrdersByStatus(
                "trace-123", 
                OrderStatusEnum.RECEIVED
        );
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        
        verify(findOrderUseCase, times(1)).findOrdersByStatus(any(OrderStatusEnum.class));
    }

    @Test
    void shouldGetOrderByIdSuccessfully() {
        when(findOrderUseCase.findOrderById(anyLong())).thenReturn(order);
        ResponseEntity<OrderResponse> response = productionController.getOrderById(
                "trace-123", 
                order.getId()
        );
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(order.getId(), response.getBody().getId());
        
        verify(findOrderUseCase, times(1)).findOrderById(anyLong());
    }

    @Test
    void shouldGetActiveOrdersSuccessfully() {
        List<Order> orders = Arrays.asList(order);
        when(findOrderUseCase.findActiveOrders()).thenReturn(orders);
        ResponseEntity<List<OrderResponse>> response = productionController.getAllActiveOrders("trace-123");
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        
        verify(findOrderUseCase, times(1)).findActiveOrders();
    }

    @Test
    void shouldReturnNoContentWhenNoActiveOrders() {
        when(findOrderUseCase.findActiveOrders()).thenReturn(Arrays.asList());
        ResponseEntity<List<OrderResponse>> response = productionController.getAllActiveOrders("trace-123");
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        
        verify(findOrderUseCase, times(1)).findActiveOrders();
    }

    @Test
    void shouldGetOrderByPaymentIdSuccessfully() {
        when(findOrderUseCase.findOrderByPaymentId(anyString())).thenReturn(order);
        ResponseEntity<OrderResponse> response = productionController.getOrderByPaymentId(
                "trace-123", 
                "PAY-123"
        );
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        verify(findOrderUseCase, times(1)).findOrderByPaymentId(anyString());
    }

    @Test
    void shouldUpdateOrderStatusSuccessfully() {
        when(updateOrderUseCase.updateOrderStatus(anyLong(), any(OrderStatusEnum.class)))
                .thenReturn(order);
        ResponseEntity<OrderResponse> response = productionController.updateOrderStatus(
                "trace-123", 
                order.getId(),
                OrderStatusEnum.IN_PREPARATION
        );
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        verify(updateOrderUseCase, times(1)).updateOrderStatus(anyLong(), any(OrderStatusEnum.class));
    }
}

