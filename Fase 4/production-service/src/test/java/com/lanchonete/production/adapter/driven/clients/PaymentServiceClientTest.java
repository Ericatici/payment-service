package com.lanchonete.production.adapter.driven.clients;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.lanchonete.production.core.application.dto.PaymentDataDTO;
import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.mocks.OrderMock;

@ExtendWith(MockitoExtension.class)
class PaymentServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentServiceClient paymentServiceClient;

    private Order order;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentServiceClient, "paymentServiceUrl", "http://localhost:8084");
        order = OrderMock.createOrderMock();
    }

    @Test
    void shouldGetPaymentDataSuccessfully() {
        String requestTraceId = "trace-123";
        PaymentDataDTO expectedPaymentData = PaymentDataDTO.builder()
                .paymentId("PAY-12345")
                .qrCode("00020126580014br.gov.bcb.pix")
                .build();

        ResponseEntity<PaymentDataDTO> responseEntity = new ResponseEntity<>(expectedPaymentData, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(PaymentDataDTO.class)
        )).thenReturn(responseEntity);

        PaymentDataDTO result = paymentServiceClient.getPaymentData(order, requestTraceId);

        assertNotNull(result);
        assertEquals("PAY-12345", result.getPaymentId());
        assertEquals("00020126580014br.gov.bcb.pix", result.getQrCode());
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(PaymentDataDTO.class)
        );
    }

    @Test
    void shouldThrowRuntimeExceptionWhenPaymentServiceFails() {
        String requestTraceId = "trace-123";

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(PaymentDataDTO.class)
        )).thenThrow(new RestClientException("Service unavailable"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentServiceClient.getPaymentData(order, requestTraceId);
        });

        assertTrue(exception.getMessage().contains("Failed to get payment data from Payment Service"));
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(PaymentDataDTO.class)
        );
    }

    @Test
    void shouldIncludeRequestTraceIdInHeaders() {
        String requestTraceId = "trace-123";
        PaymentDataDTO expectedPaymentData = PaymentDataDTO.builder()
                .paymentId("PAY-12345")
                .qrCode("00020126580014br.gov.bcb.pix")
                .build();

        ResponseEntity<PaymentDataDTO> responseEntity = new ResponseEntity<>(expectedPaymentData, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(PaymentDataDTO.class)
        )).thenReturn(responseEntity);

        paymentServiceClient.getPaymentData(order, requestTraceId);

        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.POST),
                argThat(entity -> {
                    HttpEntity<?> httpEntity = (HttpEntity<?>) entity;
                    return httpEntity.getHeaders().containsKey("requestTraceId") &&
                           requestTraceId.equals(httpEntity.getHeaders().getFirst("requestTraceId"));
                }),
                eq(PaymentDataDTO.class)
        );
    }
}

