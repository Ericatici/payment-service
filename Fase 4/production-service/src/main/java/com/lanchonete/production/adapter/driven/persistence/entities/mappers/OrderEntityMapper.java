package com.lanchonete.production.adapter.driven.persistence.entities.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.lanchonete.production.adapter.driven.persistence.entities.mappers.OrderItemEntityMapper.fromOrderItemToOrderItemEntity;

import com.lanchonete.production.adapter.driven.persistence.entities.OrderEntity;
import com.lanchonete.production.adapter.driven.persistence.entities.OrderItemEntity;
import com.lanchonete.production.core.domain.model.Order;


public class OrderEntityMapper {

    public static OrderEntity fromOrderToOrderEntity (final Order order) {   
        OrderEntity orderEntity = OrderEntity.builder()
                                    .id(order.getId())
                                    .customerCpf(order.getCustomerCpf())
                                    .customerName(order.getCustomerName())
                                    .orderDate(order.getOrderDate())
                                    .status(order.getStatus())
                                    .paymentId(order.getPaymentId())
                                    .qrCodeData(order.getQrCodeData())
                                    .paymentStatus(order.getPaymentStatus())
                                    .totalPrice(order.getTotalPrice())
                                    .build();

        final List<OrderItemEntity> orderItems = order.getItems().stream()
                .map(item -> fromOrderItemToOrderItemEntity(item, orderEntity))
                .collect(Collectors.toCollection(ArrayList::new));  

        orderEntity.setItems(orderItems);


        if (order.getCreatedDate() != null) {
            orderEntity.setCreatedDate(order.getCreatedDate());
        }

        if (order.getUpdatedDate() != null) {
            orderEntity.setUpdatedDate(order.getUpdatedDate());
        }

        return orderEntity;
    }
    
}
