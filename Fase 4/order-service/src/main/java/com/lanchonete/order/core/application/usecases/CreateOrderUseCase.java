package com.lanchonete.order.core.application.usecases;

import com.lanchonete.order.core.domain.model.Order;

public interface CreateOrderUseCase {
    Order create(Order order);
}


