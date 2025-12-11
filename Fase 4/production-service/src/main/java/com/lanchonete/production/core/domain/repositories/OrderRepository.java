package com.lanchonete.production.core.domain.repositories;

import java.util.List;

import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;

public interface OrderRepository {

    Order saveOrder(Order order);
    List<Order> findOrdersByStatus(OrderStatusEnum status);
    List<Order> findAllOrders();
    List<Order> findOrdersByCustomerCpf(String customerCpf);
    Order findOrderById(Long orderId);
    Order updateOrder(Order order);
    Order findOrderByPaymentId(String paymentId);
    List<Order> findOrdersByStatusList(List<OrderStatusEnum> statuses);
    
}

