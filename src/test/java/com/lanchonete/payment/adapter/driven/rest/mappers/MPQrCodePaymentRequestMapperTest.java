package com.lanchonete.payment.adapter.driven.rest.mappers;

import static com.lanchonete.payment.core.application.config.Constants.DYNAMIC;
import static com.lanchonete.payment.core.application.config.Constants.ORDER_DESCRIPTION;
import static com.lanchonete.payment.core.application.config.Constants.QR;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.lanchonete.payment.adapter.driven.rest.request.MPQrCodePaymentRequest;

class MPQrCodePaymentRequestMapperTest {

    @Test
    void shouldCreateMPQrCodePaymentRequestSuccessfully() {
        Long orderId = 1L;
        BigDecimal totalPrice = new BigDecimal("50.00");
        String externalPosId = "pos-123";
        MPQrCodePaymentRequest result = MPQrCodePaymentRequestMapper.createMPQrCodePaymentRequest(orderId, totalPrice, externalPosId);

        assertNotNull(result);
        assertEquals(QR, result.getType());
        assertEquals("50.00", result.getTotalAmount());
        assertEquals(ORDER_DESCRIPTION + orderId, result.getDescription());
        assertEquals(orderId.toString(), result.getExternalReference());
        
        assertNotNull(result.getConfig());
        assertNotNull(result.getConfig().getQr());
        assertEquals(externalPosId, result.getConfig().getQr().getExternalPosId());
        assertEquals(DYNAMIC, result.getConfig().getQr().getMode());
        
        assertNotNull(result.getTransactions());
        assertNotNull(result.getTransactions().getPayments());
        assertEquals(1, result.getTransactions().getPayments().size());
        assertEquals("50.00", result.getTransactions().getPayments().get(0).getAmount());
    }

    @Test
    void shouldCreateMPQrCodePaymentRequestWithDifferentValues() {
        Long orderId = 999L;
        BigDecimal totalPrice = new BigDecimal("123.45");
        String externalPosId = "pos-456";
        MPQrCodePaymentRequest result = MPQrCodePaymentRequestMapper.createMPQrCodePaymentRequest(orderId, totalPrice, externalPosId);

        assertNotNull(result);
        assertEquals(QR, result.getType());
        assertEquals("123.45", result.getTotalAmount());
        assertEquals(ORDER_DESCRIPTION + orderId, result.getDescription());
        assertEquals("999", result.getExternalReference());
        assertEquals(externalPosId, result.getConfig().getQr().getExternalPosId());
        assertEquals("123.45", result.getTransactions().getPayments().get(0).getAmount());
    }

    @Test
    void shouldCreateMPQrCodePaymentRequestWithZeroPrice() {
        Long orderId = 1L;
        BigDecimal totalPrice = BigDecimal.ZERO;
        String externalPosId = "pos-123";
        MPQrCodePaymentRequest result = MPQrCodePaymentRequestMapper.createMPQrCodePaymentRequest(orderId, totalPrice, externalPosId);

        assertNotNull(result);
        assertEquals("0", result.getTotalAmount());
        assertEquals("0", result.getTransactions().getPayments().get(0).getAmount());
    }
}

