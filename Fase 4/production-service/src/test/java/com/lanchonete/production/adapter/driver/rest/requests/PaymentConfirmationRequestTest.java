package com.lanchonete.production.adapter.driver.rest.requests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.lanchonete.production.core.application.dto.PaymentConfirmationDTO;

class PaymentConfirmationRequestTest {

    @Test
    void shouldCreatePaymentConfirmationRequestWithBuilder() {
        PaymentConfirmationRequest request = PaymentConfirmationRequest.builder()
                .id("PAY-12345")
                .status("APPROVED")
                .totalAmount(100.50)
                .build();

        assertNotNull(request);
        assertEquals("PAY-12345", request.getId());
        assertEquals("APPROVED", request.getStatus());
        assertEquals(100.50, request.getTotalAmount());
    }

    @Test
    void shouldCreatePaymentConfirmationRequestWithNoArgsConstructor() {
        PaymentConfirmationRequest request = new PaymentConfirmationRequest();

        assertNotNull(request);
        assertNull(request.getId());
        assertNull(request.getStatus());
        assertNull(request.getTotalAmount());
    }

    @Test
    void shouldCreatePaymentConfirmationRequestWithAllArgsConstructor() {
        PaymentConfirmationRequest request = new PaymentConfirmationRequest("PAY-12345", "APPROVED", 100.50);

        assertNotNull(request);
        assertEquals("PAY-12345", request.getId());
        assertEquals("APPROVED", request.getStatus());
        assertEquals(100.50, request.getTotalAmount());
    }

    @Test
    void shouldConvertToDto() {
        PaymentConfirmationRequest request = PaymentConfirmationRequest.builder()
                .id("PAY-12345")
                .status("APPROVED")
                .totalAmount(100.50)
                .build();

        PaymentConfirmationDTO dto = request.toDto();

        assertNotNull(dto);
        assertEquals("PAY-12345", dto.getId());
        assertEquals("APPROVED", dto.getStatus());
        assertEquals(100.50, dto.getTotalAmount());
    }

    @Test
    void shouldSetAndGetFields() {
        PaymentConfirmationRequest request = new PaymentConfirmationRequest();
        request.setId("PAY-12345");
        request.setStatus("APPROVED");
        request.setTotalAmount(100.50);

        assertEquals("PAY-12345", request.getId());
        assertEquals("APPROVED", request.getStatus());
        assertEquals(100.50, request.getTotalAmount());
    }
}

