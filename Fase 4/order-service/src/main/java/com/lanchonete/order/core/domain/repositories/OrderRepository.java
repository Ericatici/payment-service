package com.lanchonete.order.core.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.lanchonete.order.core.domain.model.Order;

public interface OrderRepository {

    Order saveOrder(Order order);
    Optional<Order> findById(Long id);
    List<Order> findAll();
    List<Order> findActiveOrders();
    void deleteById(Long id);
}


