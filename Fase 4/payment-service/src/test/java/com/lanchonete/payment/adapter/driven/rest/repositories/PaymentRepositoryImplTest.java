package com.lanchonete.payment.adapter.driven.rest.repositories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import com.lanchonete.payment.adapter.driven.rest.mercadopago.MercadoPagoGatewayRepository;
import com.lanchonete.payment.adapter.driven.rest.response.MPPaymentConfirmationResponse;
import com.lanchonete.payment.adapter.driven.rest.response.MPQrCodePaymentResponse;
import com.lanchonete.payment.core.domain.model.PaymentConfirmation;
import com.lanchonete.payment.core.domain.model.PaymentData;

@ExtendWith(MockitoExtension.class)
class PaymentRepositoryImplTest {

    @Mock
    private MercadoPagoGatewayRepository mercadoPagoGateway;

    @InjectMocks
    private PaymentRepositoryImpl paymentRepository;

    private Long orderId;
    private BigDecimal totalPrice;
    private MPQrCodePaymentResponse mpQrCodePaymentResponse;
    private MPPaymentConfirmationResponse mpPaymentConfirmationResponse;

    @BeforeEach
    void setUp() {
        orderId = 1L;
        totalPrice = new BigDecimal("50.00");

        mpQrCodePaymentResponse = MPQrCodePaymentResponse.builder()
                .id("mp-payment-123")
                .status("pending")
                .externalReference("1")
                .typeResponse(MPQrCodePaymentResponse.TypeResponse.builder()
                        .qrData("00020126580014br.gov.bcb.pix")
                        .build())
                .build();

        mpPaymentConfirmationResponse = new MPPaymentConfirmationResponse();
        mpPaymentConfirmationResponse.setId("mp-payment-123");
        mpPaymentConfirmationResponse.setStatus("approved");
        mpPaymentConfirmationResponse.setTotalAmount(50.0);
    }

    @Test
    void shouldGetPaymentStatusSuccessfully() {

        when(mercadoPagoGateway.getPaymentConfirmation(anyString())).thenReturn(mpPaymentConfirmationResponse);
        PaymentConfirmation result = paymentRepository.getPaymentStatus("mp-payment-123");
        assertNotNull(result);
        assertEquals("mp-payment-123", result.getId());
        assertEquals("approved", result.getStatus());
        assertEquals(50.0, result.getTotalAmount());
        verify(mercadoPagoGateway, times(1)).getPaymentConfirmation("mp-payment-123");
    }

    @Test
    void shouldReturnNullWhenPaymentConfirmationNotFound() {

        when(mercadoPagoGateway.getPaymentConfirmation(anyString())).thenReturn(null);
        PaymentConfirmation result = paymentRepository.getPaymentStatus("mp-payment-123");
        assertNull(result);
        verify(mercadoPagoGateway, times(1)).getPaymentConfirmation("mp-payment-123");
    }

    @Test
    void shouldGetPaymentDataSuccessfully() {
        when(mercadoPagoGateway.createQrCodeForPayment(anyLong(), any())).thenReturn(mpQrCodePaymentResponse);

        PaymentData result = paymentRepository.getPaymentData(orderId, totalPrice);

        assertNotNull(result);
        assertEquals("mp-payment-123", result.getPaymentId());
        assertEquals("00020126580014br.gov.bcb.pix", result.getQrCode());
        verify(mercadoPagoGateway, times(1)).createQrCodeForPayment(orderId, totalPrice);
    }

    @Test
    void shouldReturnNullWhenPaymentDataNotFound() {
        when(mercadoPagoGateway.createQrCodeForPayment(anyLong(), any())).thenReturn(null);

        PaymentData result = paymentRepository.getPaymentData(orderId, totalPrice);

        assertNull(result);
        verify(mercadoPagoGateway, times(1)).createQrCodeForPayment(orderId, totalPrice);
    }

    @Test
    void shouldHandleNullQrDataInPaymentResponse() {
        MPQrCodePaymentResponse responseWithNullQr = MPQrCodePaymentResponse.builder()
                .id("mp-payment-456")
                .status("pending")
                .externalReference("2")
                .typeResponse(null)
                .build();
        when(mercadoPagoGateway.createQrCodeForPayment(anyLong(), any())).thenReturn(responseWithNullQr);

        PaymentData result = paymentRepository.getPaymentData(orderId, totalPrice);

        assertNotNull(result);
        assertEquals("mp-payment-456", result.getPaymentId());
        assertNull(result.getQrCode());
        verify(mercadoPagoGateway, times(1)).createQrCodeForPayment(orderId, totalPrice);
    }
}


