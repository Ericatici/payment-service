package com.lanchonete.payment.core.application.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String REQUEST_TRACE_ID = "requestTraceId";
    public static final String SLASH = "/";
    public static final String CLIENT_CREDENTIALS = "client_credentials";
    public static final String X_IDEMPOTENCY_KEY = "X-Idempotency-Key";
    public static final String DYNAMIC = "dynamic";
    public static final String QR = "qr"; 
    public static final String ORDER_DESCRIPTION = "Order payment: ";
    
}

