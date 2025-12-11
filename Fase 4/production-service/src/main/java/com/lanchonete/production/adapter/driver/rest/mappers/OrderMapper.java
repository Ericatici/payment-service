package com.lanchonete.production.adapter.driver.rest.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.lanchonete.production.adapter.driver.rest.responses.OrderResponse;
import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.OrderItem;

public class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toOrderResponse(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems() != null 
            ? order.getItems().stream()
                .map(OrderMapper::toOrderItemResponse)
                .collect(Collectors.toCollection(ArrayList::new)) 
            : new ArrayList<>();

        return OrderResponse.builder()
                .id(order.getId())
                .customerCpf(order.getCustomerCpf())
                .customerName(order.getCustomerName())
                .items(itemResponses)
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentId(order.getPaymentId())
                .qrCodeData(order.getQrCodeData())
                .totalPrice(order.getTotalPrice())
                .createdDate(order.getCreatedDate())
                .updatedDate(order.getUpdatedDate())
                .build();
    }

    private static OrderResponse.OrderItemResponse toOrderItemResponse(OrderItem item) {
        return OrderResponse.OrderItemResponse.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .itemPrice(item.getItemPrice())
                .itemsTotalPrice(item.getItemsTotalPrice())
                .build();
    }
}

