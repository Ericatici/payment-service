package com.lanchonete.payment.adapter.driven.rest.mercadopago;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import org.apache.logging.log4j.ThreadContext;
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

import com.lanchonete.payment.adapter.driven.rest.request.MPAuthRequest;
import com.lanchonete.payment.adapter.driven.rest.response.MPAuthResponse;
import com.lanchonete.payment.adapter.driven.rest.response.MPPaymentConfirmationResponse;
import com.lanchonete.payment.adapter.driven.rest.response.MPQrCodePaymentResponse;
import com.lanchonete.payment.core.domain.exceptions.MercadoPagoIntegrationException;

@ExtendWith(MockitoExtension.class)
class MercadoPagoGatewayRepositoryTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MercadoPagoGatewayRepository mercadoPagoGatewayRepository;

    private MPPaymentConfirmationResponse mpPaymentConfirmationResponse;
    private MPQrCodePaymentResponse mpQrCodePaymentResponse;
    private MPAuthResponse mpAuthResponse;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mercadoPagoGatewayRepository, "clientId", "test-client-id");
        ReflectionTestUtils.setField(mercadoPagoGatewayRepository, "secretId", "test-secret-id");
        ReflectionTestUtils.setField(mercadoPagoGatewayRepository, "mpUrl", "https://api.mercadopago.com");
        ReflectionTestUtils.setField(mercadoPagoGatewayRepository, "authPath", "/oauth/token");
        ReflectionTestUtils.setField(mercadoPagoGatewayRepository, "ordersPath", "/instore/orders");
        ReflectionTestUtils.setField(mercadoPagoGatewayRepository, "externalPosId", "pos-123");

        mpPaymentConfirmationResponse = new MPPaymentConfirmationResponse();
        mpPaymentConfirmationResponse.setId("mp-payment-123");
        mpPaymentConfirmationResponse.setStatus("approved");
        mpPaymentConfirmationResponse.setTotalAmount(50.0);

        mpQrCodePaymentResponse = MPQrCodePaymentResponse.builder()
                .id("mp-payment-123")
                .status("pending")
                .externalReference("1")
                .typeResponse(MPQrCodePaymentResponse.TypeResponse.builder()
                        .qrData("00020126580014br.gov.bcb.pix")
                        .build())
                .build();

        mpAuthResponse = new MPAuthResponse();
        mpAuthResponse.setAccessToken("test-access-token");
        mpAuthResponse.setExpiresIn(3600L);
    }

    @Test
    void shouldGetPaymentConfirmationSuccessfully() {
        ResponseEntity<MPAuthResponse> authResponseEntity = 
                new ResponseEntity<>(mpAuthResponse, HttpStatus.OK);
        ResponseEntity<MPPaymentConfirmationResponse> responseEntity = 
                new ResponseEntity<>(mpPaymentConfirmationResponse, HttpStatus.OK);
        
        when(restTemplate.postForEntity(anyString(), any(MPAuthRequest.class), eq(MPAuthResponse.class)))
                .thenReturn(authResponseEntity);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), 
                eq(MPPaymentConfirmationResponse.class))).thenReturn(responseEntity);

        MPPaymentConfirmationResponse result = mercadoPagoGatewayRepository.getPaymentConfirmation("mp-payment-123");

        assertNotNull(result);
        assertEquals("mp-payment-123", result.getId());
        assertEquals("approved", result.getStatus());
        assertEquals(50.0, result.getTotalAmount());
    }

    @Test
    void shouldThrowExceptionWhenGetPaymentConfirmationFails() {
        ResponseEntity<MPAuthResponse> authResponseEntity = 
                new ResponseEntity<>(mpAuthResponse, HttpStatus.OK);
        
        when(restTemplate.postForEntity(anyString(), any(MPAuthRequest.class), eq(MPAuthResponse.class)))
                .thenReturn(authResponseEntity);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), 
                eq(MPPaymentConfirmationResponse.class)))
                .thenThrow(new RestClientException("Connection error"));
        
        assertThrows(MercadoPagoIntegrationException.class, () -> {
            mercadoPagoGatewayRepository.getPaymentConfirmation("mp-payment-123");
        });
    }

    @Test
    void shouldCreateQrCodeForPaymentSuccessfully() {

        ResponseEntity<MPAuthResponse> authResponseEntity = 
                new ResponseEntity<>(mpAuthResponse, HttpStatus.OK);
        ResponseEntity<MPQrCodePaymentResponse> qrResponseEntity = 
                new ResponseEntity<>(mpQrCodePaymentResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(MPAuthRequest.class), eq(MPAuthResponse.class)))
                .thenReturn(authResponseEntity);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(MPQrCodePaymentResponse.class)))
                .thenReturn(qrResponseEntity);
        ThreadContext.put("requestTraceId", "trace-123");
        MPQrCodePaymentResponse result = mercadoPagoGatewayRepository.createQrCodeForPayment(1L, new BigDecimal("50.00"));
        assertNotNull(result);
        assertEquals("mp-payment-123", result.getId());
        assertEquals("pending", result.getStatus());
        assertNotNull(result.getTypeResponse());
        assertEquals("00020126580014br.gov.bcb.pix", result.getTypeResponse().getQrData());
        ThreadContext.clearAll();
    }

    @Test
    void shouldThrowExceptionWhenCreateQrCodeFails() {

        ResponseEntity<MPAuthResponse> authResponseEntity = 
                new ResponseEntity<>(mpAuthResponse, HttpStatus.OK);
        
        when(restTemplate.postForEntity(anyString(), any(MPAuthRequest.class), eq(MPAuthResponse.class)))
                .thenReturn(authResponseEntity);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(MPQrCodePaymentResponse.class)))
                .thenThrow(new RestClientException("Connection error"));
        assertThrows(MercadoPagoIntegrationException.class, () -> {
            mercadoPagoGatewayRepository.createQrCodeForPayment(1L, new BigDecimal("50.00"));
        });
    }

    @Test
    void shouldRefreshTokenWhenTokenIsExpired() {

        ResponseEntity<MPAuthResponse> authResponseEntity = 
                new ResponseEntity<>(mpAuthResponse, HttpStatus.OK);
        ResponseEntity<MPPaymentConfirmationResponse> paymentResponseEntity = 
                new ResponseEntity<>(mpPaymentConfirmationResponse, HttpStatus.OK);
        ReflectionTestUtils.setField(mercadoPagoGatewayRepository, "accessToken", "old-token");
        ReflectionTestUtils.setField(mercadoPagoGatewayRepository, "tokenExpiryTime", 
                LocalDateTime.now().minus(Duration.ofMinutes(10)));

        when(restTemplate.postForEntity(anyString(), any(MPAuthRequest.class), eq(MPAuthResponse.class)))
                .thenReturn(authResponseEntity);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), 
                eq(MPPaymentConfirmationResponse.class))).thenReturn(paymentResponseEntity);
        MPPaymentConfirmationResponse result = mercadoPagoGatewayRepository.getPaymentConfirmation("mp-payment-123");
        assertNotNull(result);

        verify(restTemplate, times(1)).postForEntity(anyString(), any(MPAuthRequest.class), eq(MPAuthResponse.class));
    }

    @Test
    void shouldThrowExceptionWhenTokenRefreshFails() {

        when(restTemplate.postForEntity(anyString(), any(MPAuthRequest.class), eq(MPAuthResponse.class)))
                .thenThrow(new RestClientException("Auth error"));
        assertThrows(MercadoPagoIntegrationException.class, () -> {
            mercadoPagoGatewayRepository.getPaymentConfirmation("mp-payment-123");
        });
    }

    @Test
    void shouldThrowExceptionWhenAuthResponseIsNull() {

        ResponseEntity<MPAuthResponse> authResponseEntity = 
                new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(MPAuthRequest.class), eq(MPAuthResponse.class)))
                .thenReturn(authResponseEntity);
        assertThrows(MercadoPagoIntegrationException.class, () -> {
            mercadoPagoGatewayRepository.getPaymentConfirmation("mp-payment-123");
        });
    }

    @Test
    void shouldThrowExceptionWhenAccessTokenIsNull() {

        MPAuthResponse invalidAuthResponse = new MPAuthResponse();
        invalidAuthResponse.setAccessToken(null);
        invalidAuthResponse.setExpiresIn(3600L);
        
        ResponseEntity<MPAuthResponse> authResponseEntity = 
                new ResponseEntity<>(invalidAuthResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(MPAuthRequest.class), eq(MPAuthResponse.class)))
                .thenReturn(authResponseEntity);
        assertThrows(MercadoPagoIntegrationException.class, () -> {
            mercadoPagoGatewayRepository.getPaymentConfirmation("mp-payment-123");
        });
    }
}

