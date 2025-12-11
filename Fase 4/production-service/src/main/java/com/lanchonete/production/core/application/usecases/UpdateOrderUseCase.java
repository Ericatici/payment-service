package com.lanchonete.production.core.application.usecases;

import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;

public interface UpdateOrderUseCase {
    Order updateOrderStatus(Long id, OrderStatusEnum status);
}

