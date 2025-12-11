package com.lanchonete.payment.core.application.services;

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

import com.lanchonete.payment.core.domain.exceptions.OrderNotFoundException;
import com.lanchonete.payment.core.domain.model.PaymentConfirmation;
import com.lanchonete.payment.core.domain.model.PaymentStatus;
import com.lanchonete.payment.core.domain.model.enums.PaymentStatusEnum;
import com.lanchonete.payment.core.domain.repositories.OrderRepository;
import com.lanchonete.payment.core.domain.repositories.PaymentRepository;
import com.lanchonete.payment.mocks.OrderDTOMock;
import com.lanchonete.payment.mocks.PaymentConfirmationMock;

@ExtendWith(MockitoExtension.class)
class ConsultPaymentStatusServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private ConsultPaymentStatusService consultPaymentStatusService;

    private OrderRepository.Order order;
    private OrderRepository.Order updatedOrder;
    private PaymentConfirmation paymentConfirmation;

    @BeforeEach
    void setUp() {
        com.lanchonete.payment.core.application.dto.OrderDTO orderDTO = OrderDTOMock.createOrderDTOWithPayment();
        order = new OrderRepository.Order(
            orderDTO.getId(),
            orderDTO.getCustomerCpf(),
            orderDTO.getPaymentId(),
            orderDTO.getPaymentStatus()
        );
        
        com.lanchonete.payment.core.application.dto.OrderDTO updatedOrderDTO = OrderDTOMock.createOrderDTOWithPayment();
        updatedOrderDTO.setPaymentStatus(PaymentStatusEnum.APPROVED);
        updatedOrder = new OrderRepository.Order(
            updatedOrderDTO.getId(),
            updatedOrderDTO.getCustomerCpf(),
            updatedOrderDTO.getPaymentId(),
            updatedOrderDTO.getPaymentStatus()
        );
        
        paymentConfirmation = PaymentConfirmationMock.createApprovedPaymentMock();
    }

    @Test
    void shouldReturnPaymentStatusWhenOrderAlreadyInFinalState() {
        OrderRepository.Order approvedOrder = new OrderRepository.Order(
            order.getId(),
            order.getCustomerCpf(),
            order.getPaymentId(),
            PaymentStatusEnum.APPROVED
        );
        when(orderRepository.getOrderById(anyLong())).thenReturn(approvedOrder);

        PaymentStatus result = consultPaymentStatusService.getPaymentStatus(order.getId());

        assertNotNull(result);
        assertEquals(order.getId(), result.getOrderId());
        assertEquals(PaymentStatusEnum.APPROVED, result.getPaymentStatus());
        
        verify(orderRepository, times(1)).getOrderById(order.getId());
        verify(paymentRepository, never()).getPaymentStatus(anyString());
    }

    @Test
    void shouldConsultMercadoPagoWhenOrderIsPending() {
        when(orderRepository.getOrderById(anyLong())).thenReturn(order);
        when(paymentRepository.getPaymentStatus(order.getPaymentId())).thenReturn(paymentConfirmation);
        when(orderRepository.getOrderById(anyLong())).thenReturn(order, updatedOrder);

        PaymentStatus result = consultPaymentStatusService.getPaymentStatus(order.getId());

        assertNotNull(result);
        assertEquals(order.getId(), result.getOrderId());
        
        verify(orderRepository, times(2)).getOrderById(order.getId());
        verify(paymentRepository, times(1)).getPaymentStatus(order.getPaymentId());
        verify(orderRepository, times(1)).updateOrderPaymentStatus(anyLong(), any(PaymentConfirmation.class));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        when(orderRepository.getOrderById(anyLong())).thenReturn(null);

        assertThrows(OrderNotFoundException.class, () -> {
            consultPaymentStatusService.getPaymentStatus(999L);
        });

        verify(orderRepository, times(1)).getOrderById(999L);
        verify(paymentRepository, never()).getPaymentStatus(anyString());
    }
}

