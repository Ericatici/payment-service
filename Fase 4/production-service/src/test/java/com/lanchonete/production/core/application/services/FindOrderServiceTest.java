package com.lanchonete.production.core.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

import com.lanchonete.production.core.domain.exceptions.OrderNotFoundException;
import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.production.core.domain.repositories.OrderRepository;
import com.lanchonete.production.mocks.OrderMock;

@ExtendWith(MockitoExtension.class)
class FindOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private FindOrderService findOrderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = OrderMock.createOrderMock();
    }

    @Test
    void shouldFindOrderByIdSuccessfully() {
        when(orderRepository.findOrderById(anyLong())).thenReturn(order);
        Order result = findOrderService.findOrderById(order.getId());
        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        assertEquals(order.getCustomerCpf(), result.getCustomerCpf());
        
        verify(orderRepository, times(1)).findOrderById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFoundById() {
        Long orderId = 999L;
        when(orderRepository.findOrderById(orderId)).thenReturn(null);
        assertThrows(OrderNotFoundException.class, () -> {
            findOrderService.findOrderById(orderId);
        });

        verify(orderRepository, times(1)).findOrderById(orderId);
    }

    @Test
    void shouldFindOrderByPaymentIdSuccessfully() {
        String paymentId = "PAY-123";
        when(orderRepository.findOrderByPaymentId(anyString())).thenReturn(order);
        Order result = findOrderService.findOrderByPaymentId(paymentId);
        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        
        verify(orderRepository, times(1)).findOrderByPaymentId(anyString());
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFoundByPaymentId() {
        String paymentId = "PAY-999";
        when(orderRepository.findOrderByPaymentId(paymentId)).thenReturn(null);
        assertThrows(OrderNotFoundException.class, () -> {
            findOrderService.findOrderByPaymentId(paymentId);
        });

        verify(orderRepository, times(1)).findOrderByPaymentId(paymentId);
    }

    @Test
    void shouldFindAllOrdersSuccessfully() {
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findAllOrders()).thenReturn(orders);
        List<Order> result = findOrderService.findAllOrders();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(order.getId(), result.get(0).getId());
        
        verify(orderRepository, times(1)).findAllOrders();
    }

    @Test
    void shouldFindOrdersByStatusSuccessfully() {
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findOrdersByStatus(any(OrderStatusEnum.class))).thenReturn(orders);
        List<Order> result = findOrderService.findOrdersByStatus(OrderStatusEnum.RECEIVED);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        
        verify(orderRepository, times(1)).findOrdersByStatus(any(OrderStatusEnum.class));
    }

    @Test
    void shouldFindActiveOrdersSuccessfully() {
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findOrdersByStatusList(anyList())).thenReturn(orders);
        List<Order> result = findOrderService.findActiveOrders();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        
        verify(orderRepository, times(1)).findOrdersByStatusList(anyList());
    }
}

