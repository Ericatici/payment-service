package com.lanchonete.production.core.application.config;

import java.util.List;

import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final List<OrderStatusEnum> ACTIVE_STATUSES = List.of(OrderStatusEnum.RECEIVED, OrderStatusEnum.IN_PREPARATION, OrderStatusEnum.READY);
    public static final String PROCESSED_STATUS = "processed";
    public static final String EXPIRED_STATUS = "expired";
    public static final String REFUNDED_STATUS = "refunded";
    public static final String CANCELED_STATUS = "canceled";
    
}

