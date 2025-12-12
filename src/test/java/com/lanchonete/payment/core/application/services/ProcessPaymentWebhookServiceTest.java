package com.lanchonete.payment.core.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lanchonete.payment.core.application.dto.PaymentConfirmationDTO;
import com.lanchonete.payment.core.domain.exceptions.InvalidPaymentException;
import com.lanchonete.payment.core.domain.exceptions.OrderNotFoundException;
import com.lanchonete.payment.core.domain.model.PaymentConfirmation;
import com.lanchonete.payment.core.domain.repositories.OrderRepository;
import com.lanchonete.payment.core.domain.repositories.PaymentRepository;
import com.lanchonete.payment.mocks.OrderDTOMock;
import com.lanchonete.payment.mocks.PaymentConfirmationMock;

@ExtendWith(MockitoExtension.class)
class ProcessPaymentWebhookServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private ProcessPaymentWebhookService processPaymentWebhookService;

    private OrderRepository.Order order;
    private PaymentConfirmation paymentConfirmation;
    private PaymentConfirmationDTO paymentConfirmationDTO;

    @BeforeEach
    void setUp() {
        com.lanchonete.payment.core.application.dto.OrderDTO orderDTO = OrderDTOMock.createOrderDTOWithPayment();
        order = new OrderRepository.Order(
            orderDTO.getId(),
            orderDTO.getCustomerCpf(),
            orderDTO.getPaymentId(),
            orderDTO.getPaymentStatus()
        );
        
        paymentConfirmation = PaymentConfirmationMock.createApprovedPaymentMock();
        
        paymentConfirmationDTO = PaymentConfirmationDTO.builder()
                .data(PaymentConfirmationDTO.PaymentConfirmationDataDTO.builder()
                        .id("mp-payment-123")
                        .build())
                .build();
    }

    @Test
    void shouldProcessPaymentConfirmationSuccessfully() {
        when(orderRepository.getOrderByPaymentId(anyString())).thenReturn(order);
        when(paymentRepository.getPaymentStatus(anyString())).thenReturn(paymentConfirmation);

        assertDoesNotThrow(() -> {
            processPaymentWebhookService.processPaymentConfirmation(paymentConfirmationDTO);
        });

        verify(orderRepository, times(1)).getOrderByPaymentId("mp-payment-123");
        verify(paymentRepository, times(1)).getPaymentStatus("mp-payment-123");
        verify(orderRepository, times(1)).updateOrderPaymentStatus(anyLong(), any(PaymentConfirmation.class));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        when(orderRepository.getOrderByPaymentId(anyString())).thenReturn(null);

        assertThrows(OrderNotFoundException.class, () -> {
            processPaymentWebhookService.processPaymentConfirmation(paymentConfirmationDTO);
        });

        verify(orderRepository, times(1)).getOrderByPaymentId("mp-payment-123");
        verify(paymentRepository, never()).getPaymentStatus(anyString());
        verify(orderRepository, never()).updateOrderPaymentStatus(anyLong(), any(PaymentConfirmation.class));
    }

    @Test
    void shouldThrowExceptionWhenPaymentConfirmationNotReceived() {
        when(orderRepository.getOrderByPaymentId(anyString())).thenReturn(order);
        when(paymentRepository.getPaymentStatus(anyString())).thenReturn(null);

        assertThrows(InvalidPaymentException.class, () -> {
            processPaymentWebhookService.processPaymentConfirmation(paymentConfirmationDTO);
        });

        verify(orderRepository, times(1)).getOrderByPaymentId("mp-payment-123");
        verify(paymentRepository, times(1)).getPaymentStatus("mp-payment-123");
        verify(orderRepository, never()).updateOrderPaymentStatus(anyLong(), any(PaymentConfirmation.class));
    }
}

