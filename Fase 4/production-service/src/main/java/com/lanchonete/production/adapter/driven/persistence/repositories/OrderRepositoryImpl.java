package com.lanchonete.production.adapter.driven.persistence.repositories;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import static com.lanchonete.production.adapter.driven.persistence.entities.mappers.OrderEntityMapper.fromOrderToOrderEntity;

import com.lanchonete.production.adapter.driven.persistence.entities.OrderEntity;
import com.lanchonete.production.adapter.driven.persistence.repositories.jpa.JpaOrderRepository;
import com.lanchonete.production.core.domain.exceptions.OrderNotFoundException;
import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.production.core.domain.repositories.OrderRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;

    @Override
    public Order saveOrder(Order order) {
        final OrderEntity orderEntity = fromOrderToOrderEntity(order);

        final OrderEntity savedOrderEntity = jpaOrderRepository.save(orderEntity);

        return savedOrderEntity.toOrder();
    }

    @Override
    public List<Order> findOrdersByStatus(OrderStatusEnum status) {
        return jpaOrderRepository.findByStatus(status).stream()
                .map(OrderEntity::toOrder)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findAllOrders() {
        return jpaOrderRepository.findAll().stream()
                .map(OrderEntity::toOrder)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findOrdersByCustomerCpf(String customerCpf) {
        return jpaOrderRepository.findByCustomerCpf(customerCpf).stream()
                .map(OrderEntity::toOrder)
                .collect(Collectors.toList());
    }

    @Override
    public Order findOrderById(Long orderId) {
        return jpaOrderRepository.findById(orderId)
                .map(OrderEntity::toOrder)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
    }

    @Override
    public Order updateOrder(Order order) {
        final OrderEntity orderEntity = fromOrderToOrderEntity(order);

        final OrderEntity updatedProduct = jpaOrderRepository.save(orderEntity);

        return updatedProduct.toOrder();
    }

    @Override
    public Order findOrderByPaymentId(String paymentId) {
        return jpaOrderRepository.findByPaymentId(paymentId)
                .map(OrderEntity::toOrder)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with paymentId: " + paymentId));
    }

    @Override
    public List<Order> findOrdersByStatusList(List<OrderStatusEnum> statuses) {
        return jpaOrderRepository.findByStatusIn(statuses).stream()
                .map(OrderEntity::toOrder)
                .collect(Collectors.toList());
    }
}

