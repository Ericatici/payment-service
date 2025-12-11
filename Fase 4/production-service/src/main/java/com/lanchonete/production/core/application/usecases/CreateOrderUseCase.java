package com.lanchonete.production.core.application.usecases;

import com.lanchonete.production.core.domain.model.Order;

public interface CreateOrderUseCase {
    Order create(Order order);
}

