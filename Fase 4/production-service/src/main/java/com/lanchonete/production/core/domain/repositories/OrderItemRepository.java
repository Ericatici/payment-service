package com.lanchonete.production.core.domain.repositories;

import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.OrderItem;

public interface OrderItemRepository {

    OrderItem saveOrderItem (OrderItem orderItem, Order order);
    
}

