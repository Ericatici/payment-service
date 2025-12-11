package com.lanchonete.production.adapter.driven.persistence.entities.mappers;

import com.lanchonete.production.adapter.driven.persistence.entities.OrderEntity;
import com.lanchonete.production.adapter.driven.persistence.entities.OrderItemEntity;
import com.lanchonete.production.core.domain.model.OrderItem;

public class OrderItemEntityMapper {
    
    public static OrderItemEntity fromOrderItemToOrderItemEntity(final OrderItem orderItem, final OrderEntity order){
        OrderItemEntity orderItemEntity = OrderItemEntity.builder()
                                            .id(orderItem.getId())
                                            .order(order)
                                            .productId(orderItem.getProductId())
                                            .productName(orderItem.getProductName())
                                            .quantity(orderItem.getQuantity())
                                            .itemPrice(orderItem.getItemPrice())
                                            .itemsTotalPrice(orderItem.getItemsTotalPrice())
                                            .build();

        if (orderItem.getCreatedDate() != null) {
            orderItemEntity.setCreatedDate(orderItem.getCreatedDate());
        }

        if (orderItem.getUpdatedDate() != null) {
            orderItemEntity.setUpdatedDate(orderItem.getUpdatedDate());
        }
        
        return orderItemEntity;
    }  
}
