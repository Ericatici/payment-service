package com.lanchonete.production.adapter.driven.persistence.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lanchonete.production.adapter.driven.persistence.entities.OrderItemEntity;

@Repository
public interface JpaOrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
}

