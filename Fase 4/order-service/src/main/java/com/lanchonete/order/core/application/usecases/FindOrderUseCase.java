package com.lanchonete.order.core.application.usecases;

import java.util.List;

import com.lanchonete.order.core.domain.model.Order;

public interface FindOrderUseCase {
    Order findById(Long id);
    List<Order> findActiveOrders();
    List<Order> findAll();
}


