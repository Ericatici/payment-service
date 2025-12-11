package com.lanchonete.order.core.application.config;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContextLoggerTest {

    @BeforeEach
    void setUp() {
        ThreadContext.clearAll();
    }

    @AfterEach
    void tearDown() {
        ThreadContext.clearAll();
    }

    @Test
    void shouldSetTraceIdWhenProvided() {
        String traceId = "test-trace-id-123";

        ContextLogger.checkTraceId(traceId);

        String storedTraceId = ThreadContext.get(ContextLogger.REQUEST_TRACE_ID);
        assertNotNull(storedTraceId);
        assertEquals(traceId, storedTraceId);
    }

    @Test
    void shouldGenerateTraceIdWhenNull() {
        ContextLogger.checkTraceId(null);

        String storedTraceId = ThreadContext.get(ContextLogger.REQUEST_TRACE_ID);
        assertNotNull(storedTraceId);
        assertFalse(storedTraceId.isEmpty());
        assertTrue(storedTraceId.length() > 0);
    }

    @Test
    void shouldGenerateTraceIdWhenEmpty() {
        ContextLogger.checkTraceId("");

        String storedTraceId = ThreadContext.get(ContextLogger.REQUEST_TRACE_ID);
        assertNotNull(storedTraceId);
        assertFalse(storedTraceId.isEmpty());
    }

    @Test
    void shouldGenerateTraceIdWhenWhitespace() {
        ContextLogger.checkTraceId("   ");

        String storedTraceId = ThreadContext.get(ContextLogger.REQUEST_TRACE_ID);
        assertNotNull(storedTraceId);
        assertFalse(storedTraceId.isEmpty());
    }

    @Test
    void shouldOverrideExistingTraceId() {
        String firstTraceId = "first-trace-id";
        String secondTraceId = "second-trace-id";

        ContextLogger.checkTraceId(firstTraceId);
        ContextLogger.checkTraceId(secondTraceId);

        String storedTraceId = ThreadContext.get(ContextLogger.REQUEST_TRACE_ID);
        assertEquals(secondTraceId, storedTraceId);
    }

    @Test
    void shouldHaveCorrectConstantValue() {
        assertEquals("requestTraceId", ContextLogger.REQUEST_TRACE_ID);
    }
}

