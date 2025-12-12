package com.lanchonete.payment.adapter.driver.rest.controllers;

import static com.lanchonete.payment.core.application.config.Constants.REQUEST_TRACE_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lanchonete.payment.adapter.driver.rest.requests.PaymentConfirmationRequest;
import com.lanchonete.payment.core.application.dto.PaymentConfirmationDTO;
import com.lanchonete.payment.core.application.usecases.ConsultPaymentStatusUseCase;
import com.lanchonete.payment.core.application.usecases.GeneratePaymentQrCodeUseCase;
import com.lanchonete.payment.core.application.usecases.ProcessPaymentWebhookUseCase;
import com.lanchonete.payment.core.domain.exceptions.InvalidPaymentException;
import com.lanchonete.payment.core.domain.exceptions.MercadoPagoIntegrationException;
import com.lanchonete.payment.core.domain.exceptions.OrderNotFoundException;
import com.lanchonete.payment.core.domain.exceptions.PaymentException;
import com.lanchonete.payment.core.domain.model.PaymentStatus;
import com.lanchonete.payment.core.domain.model.enums.PaymentStatusEnum;

@WebMvcTest(PaymentController.class)
@DisplayName("PaymentController MVC Tests")
class PaymentControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ConsultPaymentStatusUseCase consultPaymentStatusUseCase;

    @MockitoBean
    private ProcessPaymentWebhookUseCase processPaymentWebhookUseCase;

    @MockitoBean
    private GeneratePaymentQrCodeUseCase generatePaymentQrCodeUseCase;

    private PaymentStatus paymentStatus;
    private PaymentConfirmationRequest paymentConfirmationRequest;

    @BeforeEach
    void setUp() {
        paymentStatus = PaymentStatus.builder()
                .orderId(1L)
                .paymentStatus(PaymentStatusEnum.APPROVED)
                .build();

        paymentConfirmationRequest = PaymentConfirmationRequest.builder()
                .id(12345L)
                .liveMode(true)
                .type("payment")
                .dateCreated(Instant.now())
                .userId(123456L)
                .apiVersion("v1")
                .action("payment.updated")
                .data(PaymentConfirmationRequest.PaymentConfirmationDataRequest.builder()
                        .id("mp-payment-123")
                        .build())
                .build();
    }

    @Nested
    @DisplayName("GET /{orderId}/payment-status")
    class GetPaymentStatusTests {

        @Test
        @DisplayName("Should return payment status successfully")
        void shouldReturnPaymentStatusSuccessfully() throws Exception {

            when(consultPaymentStatusUseCase.getPaymentStatus(anyLong())).thenReturn(paymentStatus);
            mockMvc.perform(get("/1/payment-status")
                    .header(REQUEST_TRACE_ID, "trace-123"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.orderId").value(1))
                    .andExpect(jsonPath("$.paymentStatus").value("APPROVED"));

            verify(consultPaymentStatusUseCase, times(1)).getPaymentStatus(1L);
        }

        @Test
        @DisplayName("Should return payment status without trace ID")
        void shouldReturnPaymentStatusWithoutTraceId() throws Exception {

            when(consultPaymentStatusUseCase.getPaymentStatus(anyLong())).thenReturn(paymentStatus);
            mockMvc.perform(get("/1/payment-status"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderId").value(1))
                    .andExpect(jsonPath("$.paymentStatus").value("APPROVED"));

            verify(consultPaymentStatusUseCase, times(1)).getPaymentStatus(1L);
        }

        @ParameterizedTest
        @ValueSource(longs = {1L, 999L, 12345L})
        @DisplayName("Should handle different order IDs")
        void shouldHandleDifferentOrderIds(Long orderId) throws Exception {

            PaymentStatus customPaymentStatus = PaymentStatus.builder()
                    .orderId(orderId)
                    .paymentStatus(PaymentStatusEnum.PENDING)
                    .build();
            when(consultPaymentStatusUseCase.getPaymentStatus(anyLong())).thenReturn(customPaymentStatus);
            mockMvc.perform(get("/" + orderId + "/payment-status"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderId").value(orderId))
                    .andExpect(jsonPath("$.paymentStatus").value("PENDING"));

            verify(consultPaymentStatusUseCase, times(1)).getPaymentStatus(orderId);
        }

        @Test
        @DisplayName("Should return 404 when order not found")
        void shouldReturn404WhenOrderNotFound() throws Exception {

            when(consultPaymentStatusUseCase.getPaymentStatus(anyLong()))
                    .thenThrow(new OrderNotFoundException("Order not found"));
            mockMvc.perform(get("/999/payment-status"))
                    .andExpect(status().isNotFound());

            verify(consultPaymentStatusUseCase, times(1)).getPaymentStatus(999L);
        }

        @Test
        @DisplayName("Should return 500 when payment service fails")
        void shouldReturn500WhenPaymentServiceFails() throws Exception {

            when(consultPaymentStatusUseCase.getPaymentStatus(anyLong()))
                    .thenThrow(new PaymentException("Payment service unavailable"));
            mockMvc.perform(get("/1/payment-status"))
                    .andExpect(status().isInternalServerError());

            verify(consultPaymentStatusUseCase, times(1)).getPaymentStatus(1L);
        }

        @Test
        @DisplayName("Should return 500 when MercadoPago integration fails")
        void shouldReturn500WhenMercadoPagoIntegrationFails() throws Exception {

            when(consultPaymentStatusUseCase.getPaymentStatus(anyLong()))
                    .thenThrow(new MercadoPagoIntegrationException("MercadoPago API error"));
            mockMvc.perform(get("/1/payment-status"))
                    .andExpect(status().isInternalServerError());

            verify(consultPaymentStatusUseCase, times(1)).getPaymentStatus(1L);
        }

        @Test
        @DisplayName("Should handle invalid order ID format")
        void shouldHandleInvalidOrderIdFormat() throws Exception {

            mockMvc.perform(get("/invalid/payment-status"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("POST /webhooks/payment-confirmation")
    class PostPaymentConfirmationWebhookTests {

        @Test
        @DisplayName("Should process payment confirmation webhook successfully")
        void shouldProcessPaymentConfirmationWebhookSuccessfully() throws Exception {

            doNothing().when(processPaymentWebhookUseCase).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
            mockMvc.perform(post("/webhooks/payment-confirmation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(REQUEST_TRACE_ID, "trace-456")
                    .content(objectMapper.writeValueAsString(paymentConfirmationRequest)))
                    .andExpect(status().isOk());

            verify(processPaymentWebhookUseCase, times(1)).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
        }

        @Test
        @DisplayName("Should process webhook without trace ID")
        void shouldProcessWebhookWithoutTraceId() throws Exception {

            doNothing().when(processPaymentWebhookUseCase).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
            mockMvc.perform(post("/webhooks/payment-confirmation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(paymentConfirmationRequest)))
                    .andExpect(status().isOk());

            verify(processPaymentWebhookUseCase, times(1)).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
        }

        @Test
        @DisplayName("Should return 400 for invalid JSON payload")
        void shouldReturn400ForInvalidJsonPayload() throws Exception {

            mockMvc.perform(post("/webhooks/payment-confirmation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("invalid json"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should return 400 for missing required fields")
        void shouldReturn400ForMissingRequiredFields() throws Exception {

            PaymentConfirmationRequest invalidRequest = PaymentConfirmationRequest.builder()
                    .id(12345L)
                    .action("payment.updated")

                    .build();
            mockMvc.perform(post("/webhooks/payment-confirmation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should return 404 when order not found for webhook")
        void shouldReturn404WhenOrderNotFoundForWebhook() throws Exception {

            doThrow(new OrderNotFoundException("Order not found"))
                    .when(processPaymentWebhookUseCase).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
            mockMvc.perform(post("/webhooks/payment-confirmation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(paymentConfirmationRequest)))
                    .andExpect(status().isNotFound());

            verify(processPaymentWebhookUseCase, times(1)).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
        }

        @Test
        @DisplayName("Should return 400 when payment is invalid")
        void shouldReturn400WhenPaymentIsInvalid() throws Exception {

            doThrow(new InvalidPaymentException("Invalid payment data"))
                    .when(processPaymentWebhookUseCase).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
            mockMvc.perform(post("/webhooks/payment-confirmation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(paymentConfirmationRequest)))
                    .andExpect(status().isBadRequest());

            verify(processPaymentWebhookUseCase, times(1)).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
        }

        @Test
        @DisplayName("Should return 500 when payment processing fails")
        void shouldReturn500WhenPaymentProcessingFails() throws Exception {

            doThrow(new PaymentException("Payment processing failed"))
                    .when(processPaymentWebhookUseCase).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
            mockMvc.perform(post("/webhooks/payment-confirmation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(paymentConfirmationRequest)))
                    .andExpect(status().isInternalServerError());

            verify(processPaymentWebhookUseCase, times(1)).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
        }

        @Test
        @DisplayName("Should return 500 when MercadoPago integration fails in webhook")
        void shouldReturn500WhenMercadoPagoIntegrationFailsInWebhook() throws Exception {

            doThrow(new MercadoPagoIntegrationException("MercadoPago webhook processing failed"))
                    .when(processPaymentWebhookUseCase).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
            mockMvc.perform(post("/webhooks/payment-confirmation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(paymentConfirmationRequest)))
                    .andExpect(status().isInternalServerError());

            verify(processPaymentWebhookUseCase, times(1)).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
        }

        @Test
        @DisplayName("Should reject request without content type")
        void shouldRejectRequestWithoutContentType() throws Exception {

            mockMvc.perform(post("/webhooks/payment-confirmation")
                    .content(objectMapper.writeValueAsString(paymentConfirmationRequest)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should reject request with wrong content type")
        void shouldRejectRequestWithWrongContentType() throws Exception {

            mockMvc.perform(post("/webhooks/payment-confirmation")
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("some text"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("Payment Status Enum Tests")
    class PaymentStatusEnumTests {

        @ParameterizedTest
        @MethodSource("paymentStatusProvider")
        @DisplayName("Should handle all payment status enums")
        void shouldHandleAllPaymentStatusEnums(PaymentStatusEnum statusEnum) throws Exception {

            PaymentStatus customPaymentStatus = PaymentStatus.builder()
                    .orderId(1L)
                    .paymentStatus(statusEnum)
                    .build();
            when(consultPaymentStatusUseCase.getPaymentStatus(anyLong())).thenReturn(customPaymentStatus);
            mockMvc.perform(get("/1/payment-status"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderId").value(1))
                    .andExpect(jsonPath("$.paymentStatus").value(statusEnum.name()));

            verify(consultPaymentStatusUseCase, times(1)).getPaymentStatus(1L);
        }

        static Stream<Arguments> paymentStatusProvider() {
            return Stream.of(
                    Arguments.of(PaymentStatusEnum.PENDING),
                    Arguments.of(PaymentStatusEnum.APPROVED),
                    Arguments.of(PaymentStatusEnum.REJECTED)
            );
        }
    }

    @Nested
    @DisplayName("Request Validation Tests")
    class RequestValidationTests {

        @Test
        @DisplayName("Should validate payment confirmation request structure")
        void shouldValidatePaymentConfirmationRequestStructure() throws Exception {

            PaymentConfirmationRequest validRequest = PaymentConfirmationRequest.builder()
                    .id(12345L)
                    .liveMode(true)
                    .type("payment")
                    .dateCreated(Instant.now())
                    .userId(123456L)
                    .apiVersion("v1")
                    .action("payment.updated")
                    .data(PaymentConfirmationRequest.PaymentConfirmationDataRequest.builder()
                            .id("mp-payment-123")
                            .build())
                    .build();

            doNothing().when(processPaymentWebhookUseCase).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
            mockMvc.perform(post("/webhooks/payment-confirmation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk());

            verify(processPaymentWebhookUseCase, times(1)).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
        }

    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should handle complete payment flow")
        void shouldHandleCompletePaymentFlow() throws Exception {

            PaymentStatus pendingStatus = PaymentStatus.builder()
                    .orderId(1L)
                    .paymentStatus(PaymentStatusEnum.PENDING)
                    .build();

            PaymentStatus approvedStatus = PaymentStatus.builder()
                    .orderId(1L)
                    .paymentStatus(PaymentStatusEnum.APPROVED)
                    .build();

            when(consultPaymentStatusUseCase.getPaymentStatus(1L))
                    .thenReturn(pendingStatus)
                    .thenReturn(approvedStatus);

            doNothing().when(processPaymentWebhookUseCase).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
            mockMvc.perform(get("/1/payment-status"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.paymentStatus").value("PENDING"));
            mockMvc.perform(post("/webhooks/payment-confirmation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(paymentConfirmationRequest)))
                    .andExpect(status().isOk());
            mockMvc.perform(get("/1/payment-status"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.paymentStatus").value("APPROVED"));

            verify(consultPaymentStatusUseCase, times(2)).getPaymentStatus(1L);
            verify(processPaymentWebhookUseCase, times(1)).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
        }
    }
}
