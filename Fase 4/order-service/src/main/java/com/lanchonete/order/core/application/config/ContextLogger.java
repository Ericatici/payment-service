package com.lanchonete.order.core.application.config;

import java.util.UUID;

import org.apache.logging.log4j.ThreadContext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContextLogger {

    public static final String REQUEST_TRACE_ID = "requestTraceId";

    public static void checkTraceId(String requestTraceId) {
        if (requestTraceId != null && !requestTraceId.isEmpty()) {
            ThreadContext.put(REQUEST_TRACE_ID, requestTraceId);
        } else {
            ThreadContext.put(REQUEST_TRACE_ID, UUID.randomUUID().toString());
        }
    }
}



