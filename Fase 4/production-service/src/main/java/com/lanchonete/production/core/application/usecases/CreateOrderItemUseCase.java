package com.lanchonete.production.core.application.usecases;

import com.lanchonete.production.core.domain.model.OrderItem;

public interface CreateOrderItemUseCase {
    OrderItem createOrderItem(OrderItem item);
}
