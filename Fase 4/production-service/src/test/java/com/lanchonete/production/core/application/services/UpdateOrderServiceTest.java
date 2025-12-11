package com.lanchonete.production.core.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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
class UpdateOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private UpdateOrderService updateOrderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = OrderMock.createOrderMock();
    }

    @Test
    void shouldUpdateOrderStatusSuccessfully() {
        when(orderRepository.findOrderById(anyLong())).thenReturn(order);
        when(orderRepository.saveOrder(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Order result = updateOrderService.updateOrderStatus(order.getId(), OrderStatusEnum.IN_PREPARATION);
        assertNotNull(result);
        assertEquals(OrderStatusEnum.IN_PREPARATION, result.getStatus());
        
        verify(orderRepository, times(1)).findOrderById(anyLong());
        verify(orderRepository, times(1)).saveOrder(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFoundForStatusUpdate() {
        Long orderId = 999L;
        when(orderRepository.findOrderById(orderId)).thenReturn(null);
        assertThrows(OrderNotFoundException.class, () -> {
            updateOrderService.updateOrderStatus(orderId, OrderStatusEnum.IN_PREPARATION);
        });

        verify(orderRepository, times(1)).findOrderById(orderId);
        verify(orderRepository, never()).saveOrder(any(Order.class));
    }
}

