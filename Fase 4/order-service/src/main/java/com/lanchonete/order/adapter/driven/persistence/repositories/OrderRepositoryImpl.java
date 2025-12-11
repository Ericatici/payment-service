package com.lanchonete.order.adapter.driven.persistence.repositories;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Repository;

import com.lanchonete.order.adapter.driven.clients.ProductionServiceClient;
import com.lanchonete.order.core.application.dto.OrderDTO;
import com.lanchonete.order.core.domain.model.Order;
import com.lanchonete.order.core.domain.model.OrderItem;
import com.lanchonete.order.core.domain.repositories.OrderRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@AllArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final ProductionServiceClient productionServiceClient;

    private String getRequestTraceId() {
        String traceId = ThreadContext.get("requestTraceId");
        return traceId != null ? traceId : "order-service-" + System.currentTimeMillis();
    }

    @Override
    public Order saveOrder(Order order) {
        log.debug("Delegating saveOrder to Production Service");
        
        OrderDTO orderDTO = convertToDTO(order);
        
        OrderDTO savedDTO = productionServiceClient.createOrder(orderDTO, getRequestTraceId());
        
        return convertToOrder(savedDTO);
    }

    @Override
    public Optional<Order> findById(Long id) {
        log.debug("Delegating findById to Production Service: {}", id);
        
        OrderDTO orderDTO = productionServiceClient.getOrderById(id, getRequestTraceId());
        
        return orderDTO != null ? Optional.of(convertToOrder(orderDTO)) : Optional.empty();
    }

    @Override
    public List<Order> findAll() {
        log.debug("Delegating findAll to Production Service");
        
        List<OrderDTO> orderDTOs = productionServiceClient.getAllOrders(getRequestTraceId());
        
        return orderDTOs.stream()
                .map(this::convertToOrder)
                .toList();
    }

    @Override
    public List<Order> findActiveOrders() {
        log.debug("Delegating findActiveOrders to Production Service");
        
        List<OrderDTO> orderDTOs = productionServiceClient.getActiveOrders(getRequestTraceId());
        
        return orderDTOs.stream()
                .map(this::convertToOrder)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Delete operation not supported via Production Service delegation");
        throw new UnsupportedOperationException("Delete order must be done via Production Service directly");
    }

    private OrderDTO convertToDTO(Order order) {
        List<OrderDTO.OrderItemDTO> itemDTOs = order.getItems() != null 
                ? order.getItems().stream()
                        .map(item -> OrderDTO.OrderItemDTO.builder()
                                .productId(item.getProductId())
                                .productName(item.getProductName())
                                .quantity(item.getQuantity())
                                .itemPrice(item.getItemPrice())
                                .itemsTotalPrice(item.getItemsTotalPrice())
                                .build())
                        .toList()
                : null;

        return OrderDTO.builder()
                .id(order.getId())
                .customerCpf(order.getCustomerCpf())
                .customerName(order.getCustomerName())
                .items(itemDTOs)
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .qrCodeData(order.getQrCodeData())
                .paymentId(order.getPaymentId())
                .totalPrice(order.getTotalPrice())
                .createdDate(order.getCreatedDate())
                .updatedDate(order.getUpdatedDate())
                .build();
    }

    private Order convertToOrder(OrderDTO dto) {
        List<OrderItem> items = dto.getItems() != null 
                ? dto.getItems().stream()
                        .map(itemDTO -> OrderItem.builder()
                                .productId(itemDTO.getProductId())
                                .productName(itemDTO.getProductName())
                                .quantity(itemDTO.getQuantity())
                                .itemPrice(itemDTO.getItemPrice())
                                .itemsTotalPrice(itemDTO.getItemsTotalPrice())
                                .build())
                        .toList()
                : null;

        return Order.builder()
                .id(dto.getId())
                .customerCpf(dto.getCustomerCpf())
                .customerName(dto.getCustomerName())
                .items(items)
                .orderDate(dto.getOrderDate())
                .status(dto.getStatus())
                .paymentStatus(dto.getPaymentStatus())
                .qrCodeData(dto.getQrCodeData())
                .paymentId(dto.getPaymentId())
                .totalPrice(dto.getTotalPrice())
                .createdDate(dto.getCreatedDate())
                .updatedDate(dto.getUpdatedDate())
                .build();
    }
}
