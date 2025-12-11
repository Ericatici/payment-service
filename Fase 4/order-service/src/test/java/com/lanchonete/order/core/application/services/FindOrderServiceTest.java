package com.lanchonete.order.core.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lanchonete.order.core.domain.exceptions.OrderNotFoundException;
import com.lanchonete.order.core.domain.model.Order;
import com.lanchonete.order.core.domain.repositories.OrderRepository;
import com.lanchonete.order.mocks.OrderMock;

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
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        Order result = findOrderService.findById(order.getId());

        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        assertEquals(order.getCustomerCpf(), result.getCustomerCpf());
        
        verify(orderRepository, times(1)).findById(order.getId());
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> {
            findOrderService.findById(orderId);
        });

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void shouldFindActiveOrdersSuccessfully() {
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findActiveOrders()).thenReturn(orders);

        List<Order> result = findOrderService.findActiveOrders();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(order.getId(), result.get(0).getId());
        
        verify(orderRepository, times(1)).findActiveOrders();
    }

    @Test
    void shouldFindAllOrdersSuccessfully() {
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = findOrderService.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        
        verify(orderRepository, times(1)).findAll();
    }
}


