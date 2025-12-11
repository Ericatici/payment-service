package com.lanchonete.order.core.application.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lanchonete.order.core.application.usecases.FindOrderUseCase;
import com.lanchonete.order.core.domain.exceptions.OrderNotFoundException;
import com.lanchonete.order.core.domain.model.Order;
import com.lanchonete.order.core.domain.repositories.OrderRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class FindOrderService implements FindOrderUseCase {

    private final OrderRepository orderRepository;

    @Override
    public Order findById(Long id) {
        log.info("Finding order by ID: {}", id);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Order not found with ID: {}", id);
                    return new OrderNotFoundException("Order with ID " + id + " not found");
                });
        
        log.info("Order found with ID: {}", order.getId());
        return order;
    }

    @Override
    public List<Order> findActiveOrders() {
        log.info("Finding active orders");
        
        List<Order> orders = orderRepository.findActiveOrders();
        
        log.info("Found {} active orders", orders.size());
        return orders;
    }

    @Override
    public List<Order> findAll() {
        log.info("Finding all orders");
        
        List<Order> orders = orderRepository.findAll();
        
        log.info("Found {} orders", orders.size());
        return orders;
    }
}


