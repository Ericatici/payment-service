package com.lanchonete.production.adapter.driver.rest.controllers;

import static com.lanchonete.production.core.application.config.ContextLogger.checkTraceId;
import static com.lanchonete.production.core.application.config.ContextLogger.REQUEST_TRACE_ID;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lanchonete.production.adapter.driver.rest.mappers.OrderMapper;
import com.lanchonete.production.adapter.driver.rest.requests.OrderRequest;
import com.lanchonete.production.adapter.driver.rest.requests.PaymentConfirmationRequest;
import com.lanchonete.production.adapter.driver.rest.responses.OrderResponse;
import com.lanchonete.production.core.application.config.ContextLogger;
import com.lanchonete.production.core.application.usecases.CreateOrderUseCase;
import com.lanchonete.production.core.application.usecases.FindOrderUseCase;
import com.lanchonete.production.core.application.usecases.UpdateOrderPaymentStatusUseCase;
import com.lanchonete.production.core.application.usecases.UpdateOrderUseCase;
import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.OrderItem;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Production", description = "Operations related to order production")
@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/production")
public class ProductionController {

    private final FindOrderUseCase findOrderUseCase;
    private final UpdateOrderUseCase updateOrderUseCase;
    private final CreateOrderUseCase createOrderUseCase;
    private final UpdateOrderPaymentStatusUseCase updateOrderPaymentStatusUseCase;

    @Operation(summary = "List all orders")
    @ApiResponse(responseCode = "200", description = "List of orders retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderResponse.class))))
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders(@RequestHeader(value = REQUEST_TRACE_ID, required = false) String requestTraceId) {
        checkTraceId(requestTraceId);

        log.info("Received request to list all orders");
        final List<Order> orders = findOrderUseCase.findAllOrders();
        final List<OrderResponse> ordersResponse = orders.stream().map(OrderMapper::toOrderResponse).toList();
        
        return ResponseEntity.ok(ordersResponse);
    }

    @Operation(summary = "List orders by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of orders retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid status"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/orders/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @RequestHeader(value = REQUEST_TRACE_ID, required = false) String requestTraceId,
            @PathVariable final OrderStatusEnum status) {
        checkTraceId(requestTraceId);

        log.info("Received request to list orders by status: {}", status);
        final List<Order> orders = findOrderUseCase.findOrdersByStatus(status);
        final List<OrderResponse> ordersResponse = orders.stream().map(OrderMapper::toOrderResponse).toList();
        
        return ResponseEntity.ok(ordersResponse);
    }

    @Operation(summary = "Get order by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @RequestHeader(value = REQUEST_TRACE_ID, required = false) String requestTraceId,
            @PathVariable final Long id) {
        checkTraceId(requestTraceId);

        log.info("Received request to get order by id: {}", id);
        final Order order = findOrderUseCase.findOrderById(id);
        final OrderResponse ordersResponse = OrderMapper.toOrderResponse(order);

        return ResponseEntity.ok(ordersResponse);
    }

    @Operation(summary = "Get all active orders with descriptions, sorted by status and date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of active orders retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "204", description = "No active orders found")
    })
    @GetMapping("/orders/active")
    public ResponseEntity<List<OrderResponse>> getAllActiveOrders(
            @RequestHeader(value = REQUEST_TRACE_ID, required = false) String requestTraceId) {
        checkTraceId(requestTraceId);

        log.info("Received request to get all active orders");
        final List<Order> orders = findOrderUseCase.findActiveOrders(); 
        final List<OrderResponse> orderResponses = orders.stream().map(OrderMapper::toOrderResponse).toList();

        if (orderResponses.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(orderResponses);
    }

    @Operation(summary = "Get order by paymentId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/orders/payment/{paymentId}")
    public ResponseEntity<OrderResponse> getOrderByPaymentId(
            @RequestHeader(value = REQUEST_TRACE_ID, required = false) String requestTraceId,
            @PathVariable final String paymentId) {
        checkTraceId(requestTraceId);

        log.info("Received request to get order by paymentId: {}", paymentId);
        final Order order = findOrderUseCase.findOrderByPaymentId(paymentId);
        final OrderResponse ordersResponse = OrderMapper.toOrderResponse(order);

        return ResponseEntity.ok(ordersResponse);
    }

    @Operation(summary = "Update order status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/orders/{id}/status/{status}")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @RequestHeader(value = REQUEST_TRACE_ID, required = false) String requestTraceId,
            @PathVariable final Long id,
            @PathVariable final OrderStatusEnum status) {
        checkTraceId(requestTraceId);

        log.info("Received request to update order {} status to {}", id, status);
        final Order updatedOrder = updateOrderUseCase.updateOrderStatus(id, status);
        final OrderResponse orderResponse = OrderMapper.toOrderResponse(updatedOrder);
        
        return ResponseEntity.ok(orderResponse);
    }

    @Operation(summary = "Create new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/order")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader(value = "requestTraceId", required = false) String requestTraceId,
            @RequestBody OrderRequest request) {
        
        ContextLogger.checkTraceId(requestTraceId);
        log.info("Creating order: ", request);

        Order order = Order.builder()
                .customerCpf(request.getCustomerCpf())
                .items(request.getItems().stream()
                        .map(item -> OrderItem.builder()
                                .productId(item.getProductId())
                                .productName(item.getProductName())
                                .quantity(item.getQuantity())
                                .itemPrice(item.getItemPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        Order createdOrder = createOrderUseCase.create(order);

        return ResponseEntity.status(HttpStatus.CREATED).body(OrderMapper.toOrderResponse(createdOrder));
    }
    @Operation(summary = "Update order payment status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/orders/{id}/payment-status")
    public ResponseEntity<Void> updateOrderPaymentStatus(
            @RequestHeader(value = REQUEST_TRACE_ID, required = false) String requestTraceId,
            @PathVariable final Long id,
            @RequestBody final PaymentConfirmationRequest paymentConfirmation) {
        checkTraceId(requestTraceId);

        log.info("Received request to update order {}", id);
        updateOrderPaymentStatusUseCase.updateOrderPaymentStatus(id, paymentConfirmation.toDto());
    
        
        return ResponseEntity.ok().build();
    }

}

