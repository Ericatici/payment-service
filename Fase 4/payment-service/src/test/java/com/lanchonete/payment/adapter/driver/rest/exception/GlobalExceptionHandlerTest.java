package com.lanchonete.payment.adapter.driver.rest.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lanchonete.payment.core.domain.exceptions.InvalidPaymentException;
import com.lanchonete.payment.core.domain.exceptions.MercadoPagoIntegrationException;
import com.lanchonete.payment.core.domain.exceptions.OrderNotFoundException;
import com.lanchonete.payment.core.domain.exceptions.PaymentException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void shouldHandleOrderNotFoundException() {

        OrderNotFoundException exception = new OrderNotFoundException("Order not found");
        ResponseEntity<String> response = globalExceptionHandler.handleOrderNotFoundException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Order not found", response.getBody());
    }

    @Test
    void shouldHandleInvalidPaymentException() {

        InvalidPaymentException exception = new InvalidPaymentException("Invalid payment data");
        ResponseEntity<String> response = globalExceptionHandler.handleInvalidPaymentException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid payment data", response.getBody());
    }

    @Test
    void shouldHandleMercadoPagoIntegrationException() {

        MercadoPagoIntegrationException exception = new MercadoPagoIntegrationException("MercadoPago API error");
        ResponseEntity<String> response = globalExceptionHandler.handleMercadoPagoIntegrationException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("MercadoPago API error", response.getBody());
    }

    @Test
    void shouldHandleGenericException() {

        Exception exception = new RuntimeException("Unexpected error");
        ResponseEntity<String> response = globalExceptionHandler.handleGenericException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody());
    }

    @Test
    void shouldHandlePaymentException() {

        PaymentException exception = new PaymentException("Payment processing failed");
        ResponseEntity<String> response = globalExceptionHandler.handlePaymentException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Payment processing failed", response.getBody());
    }
}

