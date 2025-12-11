package com.lanchonete.production.adapter.driven.persistence.repositories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

import com.lanchonete.production.adapter.driven.persistence.entities.OrderEntity;
import com.lanchonete.production.adapter.driven.persistence.repositories.jpa.JpaOrderRepository;
import com.lanchonete.production.core.domain.exceptions.OrderNotFoundException;
import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.production.mocks.OrderMock;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryImplTest {

    @Mock
    private JpaOrderRepository jpaOrderRepository;

    @InjectMocks
    private OrderRepositoryImpl orderRepository;

    private Order order;
    private OrderEntity orderEntity;

    @BeforeEach
    void setUp() {
        order = OrderMock.createOrderMock();
        
        orderEntity = new OrderEntity();
        orderEntity.setId(order.getId());
        orderEntity.setCustomerCpf(order.getCustomerCpf());
        orderEntity.setStatus(order.getStatus());
        orderEntity.setTotalPrice(order.getTotalPrice());
    }

    @Test
    void shouldSaveOrderSuccessfully() {
        OrderEntity savedEntity = new OrderEntity();
        savedEntity.setId(order.getId());
        savedEntity.setCustomerCpf(order.getCustomerCpf());
        savedEntity.setStatus(order.getStatus());
        savedEntity.setTotalPrice(order.getTotalPrice());
        savedEntity.setPaymentStatus(order.getPaymentStatus());
        savedEntity.setOrderDate(order.getOrderDate());
        savedEntity.setCustomerName(order.getCustomerName());
        savedEntity.setItems(new java.util.ArrayList<>());
        
        when(jpaOrderRepository.save(any(OrderEntity.class))).thenReturn(savedEntity);
        Order result = orderRepository.saveOrder(order);
        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        assertEquals(order.getCustomerCpf(), result.getCustomerCpf());
        assertEquals(order.getStatus(), result.getStatus());
        verify(jpaOrderRepository, times(1)).save(any(OrderEntity.class));
    }

    @Test
    void shouldFindOrderByIdSuccessfully() {
        when(jpaOrderRepository.findById(order.getId())).thenReturn(Optional.of(orderEntity));
        Order result = orderRepository.findOrderById(order.getId());
        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        
        verify(jpaOrderRepository, times(1)).findById(order.getId());
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        Long orderId = 999L;
        when(jpaOrderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> {
            orderRepository.findOrderById(orderId);
        });
        
        verify(jpaOrderRepository, times(1)).findById(orderId);
    }

    @Test
    void shouldFindOrdersByStatusSuccessfully() {
        List<OrderEntity> entities = Arrays.asList(orderEntity);
        when(jpaOrderRepository.findByStatusIn(any())).thenReturn(entities);
        List<Order> result = orderRepository.findOrdersByStatusList(Arrays.asList(OrderStatusEnum.RECEIVED));
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        
        verify(jpaOrderRepository, times(1)).findByStatusIn(any());
    }

    @Test
    void shouldFindOrderByPaymentIdSuccessfully() {
        String paymentId = "mp-payment-123";
        orderEntity.setPaymentId(paymentId);
        when(jpaOrderRepository.findByPaymentId(paymentId)).thenReturn(Optional.of(orderEntity));
        Order result = orderRepository.findOrderByPaymentId(paymentId);
        assertNotNull(result);
        assertEquals(paymentId, result.getPaymentId());
        
        verify(jpaOrderRepository, times(1)).findByPaymentId(paymentId);
    }
}

