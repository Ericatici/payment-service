package com.lanchonete.payment.adapter.driven.rest.mercadopago;

import static com.lanchonete.payment.core.application.config.Constants.CLIENT_CREDENTIALS;
import static com.lanchonete.payment.core.application.config.Constants.REQUEST_TRACE_ID;
import static com.lanchonete.payment.core.application.config.Constants.SLASH;
import static com.lanchonete.payment.core.application.config.Constants.X_IDEMPOTENCY_KEY;
import static com.lanchonete.payment.adapter.driven.rest.mappers.MPQrCodePaymentRequestMapper.createMPQrCodePaymentRequest;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.lanchonete.payment.adapter.driven.rest.request.MPAuthRequest;
import com.lanchonete.payment.adapter.driven.rest.request.MPQrCodePaymentRequest;
import com.lanchonete.payment.adapter.driven.rest.response.MPAuthResponse;
import com.lanchonete.payment.adapter.driven.rest.response.MPPaymentConfirmationResponse;
import com.lanchonete.payment.adapter.driven.rest.response.MPQrCodePaymentResponse;
import com.lanchonete.payment.core.domain.exceptions.MercadoPagoIntegrationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MercadoPagoGatewayRepository {

    @Value("${mercadopago.access.client}")
    private String clientId;

    @Value("${mercadopago.access.secret}")
    private String secretId;

    @Value("${mercadopago.baseUrl}")
    private String mpUrl;

    @Value("${mercadopago.path.auth}")
    private String authPath;

    @Value("${mercadopago.path.orders}")
    private String ordersPath;

    @Value("${mercadopago.externalPosId}")
    private String externalPosId;

    private String accessToken;
    private LocalDateTime tokenExpiryTime;

    @Autowired
    private final RestTemplate restTemplate;

    public MercadoPagoGatewayRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MPPaymentConfirmationResponse getPaymentConfirmation(final String paymentId){
        final String url = mpUrl + ordersPath + SLASH + paymentId;

        HttpEntity<String> entity = new HttpEntity<>(getHeaders());

        try {
            final ResponseEntity<MPPaymentConfirmationResponse> response = restTemplate.exchange(url,
                HttpMethod.GET,
                entity, 
                MPPaymentConfirmationResponse.class); 

            return response.getBody();
        } catch (Exception e) {
            throw new MercadoPagoIntegrationException("Error trying to get payment confirmation for payment id '" + paymentId +  "' from Mercado Pago.", e);
        }  
    }

    public MPQrCodePaymentResponse createQrCodeForPayment(final Long orderId, final BigDecimal totalPrice){
        final MPQrCodePaymentRequest payload = createMPQrCodePaymentRequest(orderId, totalPrice, externalPosId);
        final HttpEntity<MPQrCodePaymentRequest> request = new HttpEntity<>(payload, getHeaders(ThreadContext.get(REQUEST_TRACE_ID)));
        final String url = mpUrl + ordersPath;

        try {
            final ResponseEntity<MPQrCodePaymentResponse> response = restTemplate.postForEntity(url, request, MPQrCodePaymentResponse.class);
            return response.getBody();
        }  catch (Exception e) {
            System.out.println("Error creating order: " + e.getMessage());
            throw new MercadoPagoIntegrationException("Error trying to create new payment QR Corde for order'" + orderId +  "' on Mercado Pago.", e);
        }

    }

    private void refreshToken(){
        final String url = mpUrl + authPath;
        final MPAuthRequest authRequest = MPAuthRequest.builder()
            .clientId(clientId)
            .clientSecret(secretId)
            .granType(CLIENT_CREDENTIALS)
            .build();

        try {
            final ResponseEntity<MPAuthResponse> response = restTemplate.postForEntity(url, authRequest, MPAuthResponse.class); 

            final MPAuthResponse authResponse = response.getBody();

            if (authResponse != null && authResponse.getAccessToken() != null) {
                this.accessToken = authResponse.getAccessToken();
                this.tokenExpiryTime = LocalDateTime.now().plusSeconds(authResponse.getExpiresIn());
                log.info("Token successfuly renewed!");
            } else {
                throw new MercadoPagoIntegrationException("Failed to obtain the auth token.");
            }
        } catch (Exception e) {
            throw new MercadoPagoIntegrationException("Error trying to get auth token from Mercado Pago.", e);
        }  
    }

    private HttpHeaders getHeaders(final String requestTraceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAuthToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (requestTraceId != null) {
            headers.set(X_IDEMPOTENCY_KEY, requestTraceId);
        }
        
        return headers;
    }
    
    private HttpHeaders getHeaders() {
        return getHeaders(null);
    }

    private String getAuthToken() {
        if (accessToken == null || isTokenExpired()) {
            refreshToken();
        }
        return accessToken;
    }

     private boolean isTokenExpired() {
        return tokenExpiryTime == null || LocalDateTime.now().isAfter(tokenExpiryTime.minus(Duration.ofMinutes(5)));
    }
   
    
}

