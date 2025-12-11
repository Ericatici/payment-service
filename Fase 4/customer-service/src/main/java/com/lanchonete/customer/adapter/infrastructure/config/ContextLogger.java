package com.lanchonete.customer.adapter.infrastructure.config;

import java.util.UUID;

import org.apache.logging.log4j.ThreadContext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContextLogger {
    
    public static final String REQUEST_TRACE_ID = "requestTraceId";
    
    public static void checkTraceId(String requestTraceId){
        requestTraceId = (requestTraceId == null || requestTraceId.isBlank()) ? UUID.randomUUID().toString() : requestTraceId;
        ThreadContext.put(REQUEST_TRACE_ID, requestTraceId);
    }
}

