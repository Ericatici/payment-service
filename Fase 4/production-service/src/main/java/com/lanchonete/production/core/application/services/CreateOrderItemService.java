package com.lanchonete.production.core.application.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.lanchonete.production.core.application.usecases.CreateOrderItemUseCase;
import com.lanchonete.production.core.domain.model.OrderItem;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CreateOrderItemService implements CreateOrderItemUseCase {

    @Override
    public OrderItem createOrderItem(final OrderItem item) {
        log.info("Creating order item with product Id: {}", item.getProductId());

        OrderItem orderItem = OrderItem.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .quantity(item.getQuantity())
                    .itemPrice(item.getItemPrice())
                    .itemsTotalPrice(item.getItemPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();

        return orderItem;
    }
    
}
