package com.lanchonete.production.core.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lanchonete.production.core.application.dto.PaymentDataDTO;
import com.lanchonete.production.core.application.ports.PaymentServicePort;
import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.production.core.domain.model.enums.PaymentStatusEnum;
import com.lanchonete.production.mocks.OrderMock;

@ExtendWith(MockitoExtension.class)
class GeneratePaymentQrCodeServiceTest {

    @Mock
    private PaymentServicePort paymentServicePort;

    @InjectMocks
    private GeneratePaymentQrCodeService generatePaymentQrCodeService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = OrderMock.createOrderMock();
        ThreadContext.put("requestTraceId", "trace-123");
    }

    @Test
    void shouldGeneratePaymentQrCodeSuccessfully() {
        PaymentDataDTO paymentData = PaymentDataDTO.builder()
                .paymentId("PAY-123")
                .qrCode("00020126580014br.gov.bcb.pix")
                .build();

        when(paymentServicePort.getPaymentData(any(Order.class), anyString()))
                .thenReturn(paymentData);
        Order result = generatePaymentQrCodeService.generatePaymentQrCode(order);
        assertNotNull(result);
        assertEquals("PAY-123", result.getPaymentId());
        assertEquals("00020126580014br.gov.bcb.pix", result.getQrCodeData());
        verify(paymentServicePort, times(1)).getPaymentData(any(Order.class), anyString());
    }

    @Test
    void shouldCancelOrderWhenPaymentDataIsNull() {
        when(paymentServicePort.getPaymentData(any(Order.class), anyString()))
                .thenReturn(null);
        Order result = generatePaymentQrCodeService.generatePaymentQrCode(order);
        assertNotNull(result);
        assertEquals(PaymentStatusEnum.REJECTED, result.getPaymentStatus());
        assertEquals(OrderStatusEnum.CANCELLED, result.getStatus());
        verify(paymentServicePort, times(1)).getPaymentData(any(Order.class), anyString());
    }

    @Test
    void shouldCancelOrderWhenPaymentIdIsNull() {
        PaymentDataDTO paymentData = PaymentDataDTO.builder()
                .paymentId(null)
                .qrCode("00020126580014br.gov.bcb.pix")
                .build();

        when(paymentServicePort.getPaymentData(any(Order.class), anyString()))
                .thenReturn(paymentData);
        Order result = generatePaymentQrCodeService.generatePaymentQrCode(order);
        assertNotNull(result);
        assertEquals(PaymentStatusEnum.REJECTED, result.getPaymentStatus());
        assertEquals(OrderStatusEnum.CANCELLED, result.getStatus());
        verify(paymentServicePort, times(1)).getPaymentData(any(Order.class), anyString());
    }

    @Test
    void shouldCancelOrderWhenQrCodeIsNull() {
        PaymentDataDTO paymentData = PaymentDataDTO.builder()
                .paymentId("PAY-123")
                .qrCode(null)
                .build();

        when(paymentServicePort.getPaymentData(any(Order.class), anyString()))
                .thenReturn(paymentData);
        Order result = generatePaymentQrCodeService.generatePaymentQrCode(order);
        assertNotNull(result);
        assertEquals(PaymentStatusEnum.REJECTED, result.getPaymentStatus());
        assertEquals(OrderStatusEnum.CANCELLED, result.getStatus());
        verify(paymentServicePort, times(1)).getPaymentData(any(Order.class), anyString());
    }

    @Test
    void shouldCancelOrderWhenBothPaymentIdAndQrCodeAreNull() {
        PaymentDataDTO paymentData = PaymentDataDTO.builder()
                .paymentId(null)
                .qrCode(null)
                .build();

        when(paymentServicePort.getPaymentData(any(Order.class), anyString()))
                .thenReturn(paymentData);
        Order result = generatePaymentQrCodeService.generatePaymentQrCode(order);
        assertNotNull(result);
        assertEquals(PaymentStatusEnum.REJECTED, result.getPaymentStatus());
        assertEquals(OrderStatusEnum.CANCELLED, result.getStatus());
        verify(paymentServicePort, times(1)).getPaymentData(any(Order.class), anyString());
    }
}

