package com.lanchonete.production.adapter.driver.rest.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.lanchonete.production.core.domain.exceptions.InvalidOrderException;
import com.lanchonete.production.core.domain.exceptions.OrderNotFoundException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
    }

    @Test
    void shouldHandleOrderNotFoundException() {
        OrderNotFoundException exception = new OrderNotFoundException("Order not found with ID: 999");
        ResponseEntity<String> response = globalExceptionHandler.handleOrderNotFoundException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Order not found with ID: 999", response.getBody());
    }

    @Test
    void shouldHandleInvalidOrderException() {
        InvalidOrderException exception = new InvalidOrderException("Order is invalid");
        ResponseEntity<String> response = globalExceptionHandler.handleInvalidOrderException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Order is invalid", response.getBody());
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("order", "customerCpf", "must not be blank");
        
        when(bindingResult.getAllErrors()).thenReturn(java.util.Arrays.asList(fieldError));
        
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        ResponseEntity<Object> response = globalExceptionHandler.handleValidationExceptions(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody();
        assertEquals(1, errors.size());
        assertTrue(errors.containsKey("customerCpf"));
        assertEquals("must not be blank", errors.get("customerCpf"));
    }

    @Test
    void shouldHandleMultipleValidationErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("order", "customerCpf", "must not be blank");
        FieldError fieldError2 = new FieldError("order", "items", "must not be empty");
        
        when(bindingResult.getAllErrors()).thenReturn(java.util.Arrays.asList(fieldError1, fieldError2));
        
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        ResponseEntity<Object> response = globalExceptionHandler.handleValidationExceptions(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody();
        assertEquals(2, errors.size());
        assertTrue(errors.containsKey("customerCpf"));
        assertTrue(errors.containsKey("items"));
    }

    @Test
    void shouldHandleGeneralRuntimeException() {
        RuntimeException exception = new RuntimeException("Unexpected error occurred");
        ResponseEntity<String> response = globalExceptionHandler.handleGeneralException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody());
    }

    @Test
    void shouldHandleNullPointerException() {
        NullPointerException exception = new NullPointerException("Null pointer");
        ResponseEntity<String> response = globalExceptionHandler.handleGeneralException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody());
    }
}

