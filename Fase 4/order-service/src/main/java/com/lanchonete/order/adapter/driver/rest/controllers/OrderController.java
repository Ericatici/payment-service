package com.lanchonete.order.adapter.driver.rest.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import com.lanchonete.order.adapter.driver.rest.requests.OrderRequest;
import com.lanchonete.order.adapter.driver.rest.responses.OrderResponse;
import com.lanchonete.order.core.application.config.ContextLogger;
import com.lanchonete.order.core.application.usecases.CreateOrderUseCase;
import com.lanchonete.order.core.application.usecases.FindOrderUseCase;
import com.lanchonete.order.core.domain.model.Order;
import com.lanchonete.order.core.domain.model.OrderItem;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Orders", description = "Operations related to orders")
@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final FindOrderUseCase findOrderUseCase;

    @Operation(summary = "Create new order")
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader(value = "requestTraceId", required = false) String requestTraceId,
            @RequestBody OrderRequest request) {
        
        ContextLogger.checkTraceId(requestTraceId);
        log.info("Creating order for customer: {}", request.getCustomerCpf());

        Order order = Order.builder()
                .customerCpf(request.getCustomerCpf())
                .items(request.getItems().stream()
                        .map(item -> OrderItem.builder()
                                .productId(item.getProductId())
                                .quantity(item.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        Order createdOrder = createOrderUseCase.create(order);

        OrderResponse response = convertToResponse(createdOrder);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get order by id")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @RequestHeader(value = "requestTraceId", required = false) String requestTraceId,
            @PathVariable Long id) {
        
        ContextLogger.checkTraceId(requestTraceId);
        log.info("Getting order by ID: {}", id);

        Order order = findOrderUseCase.findById(id);
        OrderResponse response = convertToResponse(order);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all active orders")
    @GetMapping("/active")
    public ResponseEntity<List<OrderResponse>> getActiveOrders(
            @RequestHeader(value = "requestTraceId", required = false) String requestTraceId) {
        
        ContextLogger.checkTraceId(requestTraceId);
        log.info("Getting active orders");

        List<Order> orders = findOrderUseCase.findActiveOrders();
        List<OrderResponse> responses = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get all orders")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(
            @RequestHeader(value = "requestTraceId", required = false) String requestTraceId) {
        
        ContextLogger.checkTraceId(requestTraceId);
        log.info("Getting all orders");

        List<Order> orders = findOrderUseCase.findAll();
        List<OrderResponse> responses = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    private OrderResponse convertToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerCpf(order.getCustomerCpf())
                .customerName(order.getCustomerName())
                .items(order.getItems().stream()
                        .map(item -> OrderResponse.OrderItemResponse.builder()
                                .productId(item.getProductId())
                                .productName(item.getProductName())
                                .quantity(item.getQuantity())
                                .itemPrice(item.getItemPrice())
                                .itemsTotalPrice(item.getItemsTotalPrice())
                                .build())
                        .collect(Collectors.toList()))
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
}

