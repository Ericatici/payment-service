package com.lanchonete.production.core.application.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lanchonete.production.core.application.config.Constants;
import com.lanchonete.production.core.application.usecases.FindOrderUseCase;
import com.lanchonete.production.core.domain.exceptions.OrderNotFoundException;
import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.production.core.domain.repositories.OrderRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class FindOrderService implements FindOrderUseCase {

    private final OrderRepository orderRepository;

    @Override
    public Order findOrderById(Long id) {
        log.info("Finding order by ID: {}", id);
        
        Order order = orderRepository.findOrderById(id);
        
        if (order == null) {
            log.error("Order not found with ID: {}", id);
            throw new OrderNotFoundException("Order with ID " + id + " not found");
        }
        
        log.info("Order found: {}", order.getId());
        return order;
    }

    @Override
    public Order findOrderByPaymentId(String paymentId) {
        log.info("Finding order by payment ID: {}", paymentId);
        
        Order order = orderRepository.findOrderByPaymentId(paymentId);
        
        if (order == null) {
            log.error("Order not found with payment ID: {}", paymentId);
            throw new OrderNotFoundException("Order with payment ID " + paymentId + " not found");
        }
        
        log.info("Order found: {}", order.getId());
        return order;
    }

    @Override
    public List<Order> findAllOrders() {
        log.info("Finding all orders");
        
        List<Order> orders = orderRepository.findAllOrders();
        
        log.info("Found {} orders", orders.size());
        return orders;
    }

    @Override
    public List<Order> findOrdersByStatus(OrderStatusEnum status) {
        log.info("Finding orders by status: {}", status);
        
        List<Order> orders = orderRepository.findOrdersByStatus(status);
        
        log.info("Found {} orders with status {}", orders.size(), status);
        return orders;
    }

    @Override
    public List<Order> findActiveOrders() {
        log.info("Finding active orders");
        
        List<Order> orders = orderRepository.findOrdersByStatusList(Constants.ACTIVE_STATUSES);
        
        log.info("Found {} active orders", orders.size());
        return orders;
    }
}

