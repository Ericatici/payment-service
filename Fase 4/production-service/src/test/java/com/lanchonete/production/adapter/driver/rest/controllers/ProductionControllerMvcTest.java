package com.lanchonete.production.adapter.driver.rest.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.lanchonete.production.core.application.usecases.CreateOrderUseCase;
import com.lanchonete.production.core.application.usecases.FindOrderUseCase;
import com.lanchonete.production.core.application.usecases.UpdateOrderPaymentStatusUseCase;
import com.lanchonete.production.core.application.usecases.UpdateOrderUseCase;
import com.lanchonete.production.core.domain.exceptions.OrderNotFoundException;
import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.OrderItem;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.production.core.domain.model.enums.PaymentStatusEnum;
import com.lanchonete.production.mocks.OrderMock;

@WebMvcTest(ProductionController.class)
class ProductionControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FindOrderUseCase findOrderUseCase;

    @MockitoBean
    private UpdateOrderUseCase updateOrderUseCase;

    @MockitoBean
    private CreateOrderUseCase createOrderUseCase;

    @MockitoBean
    private UpdateOrderPaymentStatusUseCase updateOrderPaymentStatusUseCase;

    private Order order;

    @BeforeEach
    void setUp() {
        order = OrderMock.createOrderMock();
    }

    @Test
    void shouldGetAllOrdersSuccessfully() throws Exception {
        List<Order> orders = Arrays.asList(order);
        when(findOrderUseCase.findAllOrders()).thenReturn(orders);
        mockMvc.perform(get("/production/orders")
                .header("X-Request-Trace-Id", "trace-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(order.getId()))
                .andExpect(jsonPath("$[0].customerCpf").value(order.getCustomerCpf()))
                .andExpect(jsonPath("$[0].status").value(order.getStatus().name()))
                .andExpect(jsonPath("$[0].paymentStatus").value(order.getPaymentStatus().name()));
    }

    @Test
    void shouldGetOrdersByStatusSuccessfully() throws Exception {
        List<Order> orders = Arrays.asList(order);
        when(findOrderUseCase.findOrdersByStatus(OrderStatusEnum.RECEIVED)).thenReturn(orders);
        mockMvc.perform(get("/production/orders/status/RECEIVED")
                .header("X-Request-Trace-Id", "trace-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(order.getId()))
                .andExpect(jsonPath("$[0].status").value("RECEIVED"));
    }

    @Test
    void shouldGetOrderByIdSuccessfully() throws Exception {
        when(findOrderUseCase.findOrderById(1L)).thenReturn(order);
        mockMvc.perform(get("/production/orders/1")
                .header("X-Request-Trace-Id", "trace-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.customerCpf").value(order.getCustomerCpf()))
                .andExpect(jsonPath("$.status").value(order.getStatus().name()));
    }

    @Test
    void shouldReturnNotFoundWhenOrderByIdNotFound() throws Exception {
        when(findOrderUseCase.findOrderById(999L))
                .thenThrow(new OrderNotFoundException("Order not found with ID: 999"));
        mockMvc.perform(get("/production/orders/999")
                .header("X-Request-Trace-Id", "trace-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllActiveOrdersSuccessfully() throws Exception {
        List<Order> orders = Arrays.asList(order);
        when(findOrderUseCase.findActiveOrders()).thenReturn(orders);
        mockMvc.perform(get("/production/orders/active")
                .header("X-Request-Trace-Id", "trace-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(order.getId()));
    }

    @Test
    void shouldReturnNoContentWhenNoActiveOrders() throws Exception {
        when(findOrderUseCase.findActiveOrders()).thenReturn(Arrays.asList());
        mockMvc.perform(get("/production/orders/active")
                .header("X-Request-Trace-Id", "trace-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetOrderByPaymentIdSuccessfully() throws Exception {
        order.setPaymentId("PAY-123");
        when(findOrderUseCase.findOrderByPaymentId("PAY-123")).thenReturn(order);
        mockMvc.perform(get("/production/orders/payment/PAY-123")
                .header("X-Request-Trace-Id", "trace-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.paymentId").value("PAY-123"));
    }

    @Test
    void shouldReturnNotFoundWhenOrderByPaymentIdNotFound() throws Exception {
        when(findOrderUseCase.findOrderByPaymentId("PAY-999"))
                .thenThrow(new OrderNotFoundException("Order not found with payment ID: PAY-999"));
        mockMvc.perform(get("/production/orders/payment/PAY-999")
                .header("X-Request-Trace-Id", "trace-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateOrderStatusSuccessfully() throws Exception {
        Order updatedOrder = Order.builder()
                .id(1L)
                .customerCpf("12345678900")
                .status(OrderStatusEnum.IN_PREPARATION)
                .paymentStatus(PaymentStatusEnum.APPROVED)
                .totalPrice(new BigDecimal("25.00"))
                .orderDate(LocalDateTime.now())
                .items(Arrays.asList(OrderItem.builder()
                        .productId("PROD-001")
                        .quantity(2)
                        .itemPrice(new BigDecimal("25.00"))
                        .build()))
                .build();

        when(updateOrderUseCase.updateOrderStatus(1L, OrderStatusEnum.IN_PREPARATION))
                .thenReturn(updatedOrder);
        mockMvc.perform(patch("/production/orders/1/status/IN_PREPARATION")
                .header("X-Request-Trace-Id", "trace-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("IN_PREPARATION"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentOrderStatus() throws Exception {
        when(updateOrderUseCase.updateOrderStatus(999L, OrderStatusEnum.IN_PREPARATION))
                .thenThrow(new OrderNotFoundException("Order not found with ID: 999"));
        mockMvc.perform(patch("/production/orders/999/status/IN_PREPARATION")
                .header("X-Request-Trace-Id", "trace-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHandleInvalidStatusEnum() throws Exception {
        mockMvc.perform(get("/production/orders/status/INVALID_STATUS")
                .header("X-Request-Trace-Id", "trace-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldHandleInvalidStatusEnumInUpdate() throws Exception {
        mockMvc.perform(patch("/production/orders/1/status/INVALID_STATUS")
                .header("X-Request-Trace-Id", "trace-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldWorkWithoutTraceIdHeader() throws Exception {
        when(findOrderUseCase.findAllOrders()).thenReturn(Arrays.asList(order));
        mockMvc.perform(get("/production/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldHandleAllOrderStatuses() throws Exception {
        OrderStatusEnum[] statuses = OrderStatusEnum.values();
        
        for (OrderStatusEnum status : statuses) {
            Order orderWithStatus = Order.builder()
                    .id(1L)
                    .customerCpf("12345678900")
                    .status(status)
                    .paymentStatus(PaymentStatusEnum.PENDING)
                    .totalPrice(new BigDecimal("25.00"))
                    .orderDate(LocalDateTime.now())
                    .items(Arrays.asList(OrderItem.builder()
                            .productId("PROD-001")
                            .quantity(2)
                            .itemPrice(new BigDecimal("25.00"))
                            .build()))
                    .build();
            
            when(findOrderUseCase.findOrdersByStatus(status)).thenReturn(Arrays.asList(orderWithStatus));
            
            mockMvc.perform(get("/production/orders/status/" + status.name())
                    .header("X-Request-Trace-Id", "trace-123")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].status").value(status.name()));
        }
    }
}
