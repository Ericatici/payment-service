package com.lanchonete.production.adapter.driven.persistence.repositories.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lanchonete.production.adapter.driven.persistence.entities.OrderEntity;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;

@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByStatus(OrderStatusEnum status);
    Optional<OrderEntity> findByPaymentId(String paymentId);
    List<OrderEntity> findByStatusIn(List<OrderStatusEnum> statuses);
    List<OrderEntity> findByCustomerCpf(String customerCpf);
}

