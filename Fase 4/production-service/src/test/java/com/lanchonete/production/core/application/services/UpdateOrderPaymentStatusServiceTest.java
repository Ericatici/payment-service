package com.lanchonete.production.core.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lanchonete.production.core.application.dto.PaymentConfirmationDTO;
import com.lanchonete.production.core.domain.exceptions.OrderNotFoundException;
import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.production.core.domain.model.enums.PaymentStatusEnum;
import com.lanchonete.production.core.domain.repositories.OrderRepository;
import com.lanchonete.production.mocks.OrderMock;

@ExtendWith(MockitoExtension.class)
class UpdateOrderPaymentStatusServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private UpdateOrderPaymentStatusService updateOrderPaymentStatusService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = OrderMock.createOrderMock();
        order.setStatus(OrderStatusEnum.WAITING_PAYMENT);
        order.setPaymentStatus(PaymentStatusEnum.PENDING);
    }

    @Test
    void shouldUpdateOrderStatusToReadyWhenPaymentIsProcessed() {
        PaymentConfirmationDTO payment = PaymentConfirmationDTO.builder()
                .id("PAY-123")
                .status("processed")
                .totalAmount(50.0)
                .build();

        when(orderRepository.findOrderById(anyLong())).thenReturn(order);
        when(orderRepository.updateOrder(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        updateOrderPaymentStatusService.updateOrderPaymentStatus(order.getId(), payment);
        assertEquals(PaymentStatusEnum.APPROVED, order.getPaymentStatus());
        assertEquals(OrderStatusEnum.READY, order.getStatus());
        verify(orderRepository, times(1)).findOrderById(anyLong());
        verify(orderRepository, times(1)).updateOrder(any(Order.class));
    }

    @Test
    void shouldUpdateOrderStatusToCancelledWhenPaymentIsRefunded() {
        PaymentConfirmationDTO payment = PaymentConfirmationDTO.builder()
                .id("PAY-123")
                .status("refunded")
                .totalAmount(50.0)
                .build();

        when(orderRepository.findOrderById(anyLong())).thenReturn(order);
        when(orderRepository.updateOrder(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        updateOrderPaymentStatusService.updateOrderPaymentStatus(order.getId(), payment);
        assertEquals(PaymentStatusEnum.REJECTED, order.getPaymentStatus());
        assertEquals(OrderStatusEnum.CANCELLED, order.getStatus());
        verify(orderRepository, times(1)).findOrderById(anyLong());
        verify(orderRepository, times(1)).updateOrder(any(Order.class));
    }

    @Test
    void shouldUpdateOrderStatusToCancelledWhenPaymentIsExpired() {
        PaymentConfirmationDTO payment = PaymentConfirmationDTO.builder()
                .id("PAY-123")
                .status("expired")
                .totalAmount(50.0)
                .build();

        when(orderRepository.findOrderById(anyLong())).thenReturn(order);
        when(orderRepository.updateOrder(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        updateOrderPaymentStatusService.updateOrderPaymentStatus(order.getId(), payment);
        assertEquals(PaymentStatusEnum.REJECTED, order.getPaymentStatus());
        assertEquals(OrderStatusEnum.CANCELLED, order.getStatus());
        verify(orderRepository, times(1)).findOrderById(anyLong());
        verify(orderRepository, times(1)).updateOrder(any(Order.class));
    }

    @Test
    void shouldUpdateOrderStatusToCancelledWhenPaymentIsCanceled() {
        PaymentConfirmationDTO payment = PaymentConfirmationDTO.builder()
                .id("PAY-123")
                .status("canceled")
                .totalAmount(50.0)
                .build();

        when(orderRepository.findOrderById(anyLong())).thenReturn(order);
        when(orderRepository.updateOrder(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        updateOrderPaymentStatusService.updateOrderPaymentStatus(order.getId(), payment);
        assertEquals(PaymentStatusEnum.REJECTED, order.getPaymentStatus());
        assertEquals(OrderStatusEnum.CANCELLED, order.getStatus());
        verify(orderRepository, times(1)).findOrderById(anyLong());
        verify(orderRepository, times(1)).updateOrder(any(Order.class));
    }

    @Test
    void shouldSetPaymentStatusToPendingWhenStatusIsUnknown() {
        order.setPaymentStatus(PaymentStatusEnum.APPROVED);
        PaymentConfirmationDTO payment = PaymentConfirmationDTO.builder()
                .id("PAY-123")
                .status("unknown_status")
                .totalAmount(50.0)
                .build();

        when(orderRepository.findOrderById(anyLong())).thenReturn(order);
        when(orderRepository.updateOrder(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        updateOrderPaymentStatusService.updateOrderPaymentStatus(order.getId(), payment);
        assertEquals(PaymentStatusEnum.PENDING, order.getPaymentStatus());
        verify(orderRepository, times(1)).findOrderById(anyLong());
        verify(orderRepository, times(1)).updateOrder(any(Order.class));
    }

    @Test
    void shouldNotUpdateOrderWhenPaymentStatusIsAlreadyTheSame() {
        order.setPaymentStatus(PaymentStatusEnum.APPROVED);
        PaymentConfirmationDTO payment = PaymentConfirmationDTO.builder()
                .id("PAY-123")
                .status("processed")
                .totalAmount(50.0)
                .build();

        when(orderRepository.findOrderById(anyLong())).thenReturn(order);
        updateOrderPaymentStatusService.updateOrderPaymentStatus(order.getId(), payment);
        verify(orderRepository, times(1)).findOrderById(anyLong());
        verify(orderRepository, never()).updateOrder(any(Order.class));
    }

    @Test
    void shouldHandleNullPayment() {
        when(orderRepository.findOrderById(anyLong())).thenReturn(order);
        updateOrderPaymentStatusService.updateOrderPaymentStatus(order.getId(), null);
        verify(orderRepository, times(1)).findOrderById(anyLong());
        verify(orderRepository, never()).updateOrder(any(Order.class));
    }

    @Test
    void shouldHandleNullPaymentStatus() {
        PaymentConfirmationDTO payment = PaymentConfirmationDTO.builder()
                .id("PAY-123")
                .status(null)
                .totalAmount(50.0)
                .build();

        when(orderRepository.findOrderById(anyLong())).thenReturn(order);
        updateOrderPaymentStatusService.updateOrderPaymentStatus(order.getId(), payment);
        verify(orderRepository, times(1)).findOrderById(anyLong());
        verify(orderRepository, never()).updateOrder(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        PaymentConfirmationDTO payment = PaymentConfirmationDTO.builder()
                .id("PAY-123")
                .status("processed")
                .totalAmount(50.0)
                .build();

        when(orderRepository.findOrderById(anyLong()))
                .thenThrow(new OrderNotFoundException("Order not found"));
        assertThrows(OrderNotFoundException.class, () -> {
            updateOrderPaymentStatusService.updateOrderPaymentStatus(999L, payment);
        });

        verify(orderRepository, times(1)).findOrderById(anyLong());
        verify(orderRepository, never()).updateOrder(any(Order.class));
    }
}

