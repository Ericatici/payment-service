package com.lanchonete.production.core.application.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PaymentDataDTOTest {

    @Test
    void shouldCreatePaymentDataDTOWithBuilder() {
        PaymentDataDTO paymentData = PaymentDataDTO.builder()
                .paymentId("PAY-12345")
                .qrCode("00020126580014br.gov.bcb.pix")
                .build();

        assertNotNull(paymentData);
        assertEquals("PAY-12345", paymentData.getPaymentId());
        assertEquals("00020126580014br.gov.bcb.pix", paymentData.getQrCode());
    }

    @Test
    void shouldSetAndGetFields() {
        PaymentDataDTO paymentData = PaymentDataDTO.builder()
                .paymentId(null)
                .qrCode(null)
                .build();
        paymentData.setPaymentId("PAY-12345");
        paymentData.setQrCode("00020126580014br.gov.bcb.pix");

        assertEquals("PAY-12345", paymentData.getPaymentId());
        assertEquals("00020126580014br.gov.bcb.pix", paymentData.getQrCode());
    }

    @Test
    void shouldHandleNullValues() {
        PaymentDataDTO paymentData = PaymentDataDTO.builder()
                .paymentId(null)
                .qrCode(null)
                .build();

        assertNotNull(paymentData);
        assertNull(paymentData.getPaymentId());
        assertNull(paymentData.getQrCode());
    }
}

