package com.lanchonete.payment.adapter.driver.rest.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lanchonete.payment.adapter.driver.rest.requests.PaymentConfirmationRequest;
import com.lanchonete.payment.adapter.driver.rest.requests.PaymentDataRequest;
import com.lanchonete.payment.core.application.dto.PaymentConfirmationDTO;
import com.lanchonete.payment.core.application.usecases.ConsultPaymentStatusUseCase;
import com.lanchonete.payment.core.application.usecases.GeneratePaymentQrCodeUseCase;
import com.lanchonete.payment.core.application.usecases.ProcessPaymentWebhookUseCase;
import com.lanchonete.payment.core.domain.exceptions.OrderNotFoundException;
import com.lanchonete.payment.core.domain.model.PaymentData;
import com.lanchonete.payment.core.domain.model.PaymentStatus;
import com.lanchonete.payment.core.domain.model.enums.PaymentStatusEnum;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

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
    private PaymentDataRequest paymentDataRequest;
    private PaymentData paymentData;

    @BeforeEach
    void setUp() {
        paymentStatus = PaymentStatus.builder()
                .orderId(1L)
                .paymentStatus(PaymentStatusEnum.APPROVED)
                .build();

        paymentConfirmationRequest = PaymentConfirmationRequest.builder()
                .id(12345L)
                .action("payment.updated")
                .data(PaymentConfirmationRequest.PaymentConfirmationDataRequest.builder()
                        .id("mp-payment-123")
                        .build())
                .build();

        paymentDataRequest = PaymentDataRequest.builder()
                .orderId(1L)
                .totalPrice(java.math.BigDecimal.valueOf(50.00))
                .build();

        paymentData = PaymentData.builder()
                .paymentId("mp-payment-123")
                .qrCode("00020126580014br.gov.bcb.pix")
                .build();
    }

    @Test
    void shouldGetPaymentStatusSuccessfully() throws Exception {

        when(consultPaymentStatusUseCase.getPaymentStatus(anyLong())).thenReturn(paymentStatus);
        mockMvc.perform(get("/1/payment-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.paymentStatus").value("APPROVED"));

        verify(consultPaymentStatusUseCase, times(1)).getPaymentStatus(1L);
    }

    @Test
    void shouldReturn404WhenOrderNotFound() throws Exception {

        when(consultPaymentStatusUseCase.getPaymentStatus(anyLong()))
                .thenThrow(new OrderNotFoundException("Order not found"));
        mockMvc.perform(get("/999/payment-status"))
                .andExpect(status().isNotFound());

        verify(consultPaymentStatusUseCase, times(1)).getPaymentStatus(999L);
    }

    @Test
    void shouldProcessPaymentConfirmationWebhookSuccessfully() throws Exception {

        doNothing().when(processPaymentWebhookUseCase).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
        mockMvc.perform(post("/webhooks/payment-confirmation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentConfirmationRequest)))
                .andExpect(status().isOk());

        verify(processPaymentWebhookUseCase, times(1)).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
    }

    @Test
    void shouldReturn404WhenWebhookOrderNotFound() throws Exception {

        doThrow(new OrderNotFoundException("Order not found"))
                .when(processPaymentWebhookUseCase).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
        mockMvc.perform(post("/webhooks/payment-confirmation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentConfirmationRequest)))
                .andExpect(status().isNotFound());

        verify(processPaymentWebhookUseCase, times(1)).processPaymentConfirmation(any(PaymentConfirmationDTO.class));
    }

    @Test
    void shouldGetPaymentDataSuccessfully() throws Exception {

        when(generatePaymentQrCodeUseCase.generatePaymentQrCode(any())).thenReturn(paymentData);
        mockMvc.perform(post("/paymentData")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentDataRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("mp-payment-123"))
                .andExpect(jsonPath("$.qrCode").value("00020126580014br.gov.bcb.pix"));

        verify(generatePaymentQrCodeUseCase, times(1)).generatePaymentQrCode(any());
    }
}

