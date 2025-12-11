package com.lanchonete.production.core.application.services;

import org.springframework.stereotype.Service;

import com.lanchonete.production.core.application.usecases.UpdateOrderUseCase;
import com.lanchonete.production.core.domain.exceptions.OrderNotFoundException;
import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.production.core.domain.repositories.OrderRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class UpdateOrderService implements UpdateOrderUseCase {

    private final OrderRepository orderRepository;

    @Override
    public Order updateOrderStatus(Long id, OrderStatusEnum status) {
        log.info("Updating order {} status to {}", id, status);
        
        Order order = orderRepository.findOrderById(id);
        
        if (order == null) {
            log.error("Order not found with ID: {}", id);
            throw new OrderNotFoundException("Order with ID " + id + " not found");
        }
        
        order.setStatus(status);
        Order updatedOrder = orderRepository.saveOrder(order);
        
        log.info("Order status updated successfully");
        return updatedOrder;
    }
}

