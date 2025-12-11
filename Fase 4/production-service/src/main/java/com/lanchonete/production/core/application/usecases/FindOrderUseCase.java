package com.lanchonete.production.core.application.usecases;

import java.util.List;

import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;

public interface FindOrderUseCase {
    Order findOrderById(Long id);
    Order findOrderByPaymentId(String paymentId);
    List<Order> findAllOrders();
    List<Order> findOrdersByStatus(OrderStatusEnum status);
    List<Order> findActiveOrders();
}

