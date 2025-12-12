package com.lanchonete.payment.adapter.driver.rest.controllers;

import static com.lanchonete.payment.core.application.config.ContextLogger.checkTraceId;
import static com.lanchonete.payment.core.application.config.Constants.REQUEST_TRACE_ID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lanchonete.payment.adapter.driver.rest.mappers.PaymentDataMapper;
import com.lanchonete.payment.adapter.driver.rest.mappers.PaymentStatusMapper;
import com.lanchonete.payment.adapter.driver.rest.requests.PaymentConfirmationRequest;
import com.lanchonete.payment.adapter.driver.rest.requests.PaymentDataRequest;
import com.lanchonete.payment.adapter.driver.rest.responses.PaymentDataResponse;
import com.lanchonete.payment.adapter.driver.rest.responses.PaymentStatusResponse;
import com.lanchonete.payment.core.application.usecases.ConsultPaymentStatusUseCase;
import com.lanchonete.payment.core.application.usecases.GeneratePaymentQrCodeUseCase;
import com.lanchonete.payment.core.application.usecases.ProcessPaymentWebhookUseCase;
import com.lanchonete.payment.core.domain.model.PaymentData;
import com.lanchonete.payment.core.domain.model.PaymentStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Payments", description = "Operations related to payments")
@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping
public class PaymentController {

    private final ConsultPaymentStatusUseCase consultPaymentStatusUseCase; 
    private final ProcessPaymentWebhookUseCase processPaymentWebhookUseCase;
    private final GeneratePaymentQrCodeUseCase generatePaymentQrCodeUseCase; 

    @Operation(summary = "Consult order payment status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment status retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PaymentStatusResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{orderId}/payment-status")
    public ResponseEntity<PaymentStatusResponse> getPaymentStatus(
            @RequestHeader(value = REQUEST_TRACE_ID, required = false) String requestTraceId,
            @PathVariable final Long orderId) {
        checkTraceId(requestTraceId);

        log.info("Received request to consult payment status for order id: {}", orderId);
        final PaymentStatus paymentStatus = consultPaymentStatusUseCase.getPaymentStatus(orderId);
        final PaymentStatusResponse paymentStatusResponse = PaymentStatusMapper.toResponse(paymentStatus);

        return ResponseEntity.ok(paymentStatusResponse);
    }

    @Operation(summary = "Webhook to receive payment confirmation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment confirmation received and processed"),
            @ApiResponse(responseCode = "400", description = "Invalid webhook payload"),
            @ApiResponse(responseCode = "404", description = "Order not found for given external reference")
    })
    @PostMapping("/webhooks/payment-confirmation")
    public ResponseEntity<Void> receivePaymentConfirmation(
            @RequestHeader(value = REQUEST_TRACE_ID, required = false) String requestTraceId,
            @RequestBody final PaymentConfirmationRequest paymentConfirmationRequest) { 
        checkTraceId(requestTraceId);

        log.info("Received payment confirmation webhook for order with external reference: {}", 
                paymentConfirmationRequest.getData().getId());

        processPaymentWebhookUseCase.processPaymentConfirmation(paymentConfirmationRequest.toDto()); 
        return ResponseEntity.ok().build();
    }
    @Operation(summary = "Get payment data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment data retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PaymentStatusResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/paymentData")
    public ResponseEntity<PaymentDataResponse> getPaymentData(
            @RequestHeader(value = REQUEST_TRACE_ID, required = false) String requestTraceId,
            @RequestBody final PaymentDataRequest paymentDataRequest) {
        checkTraceId(requestTraceId);

        log.info("Received request to generate payment QR Code for order id: {}", paymentDataRequest.getOrderId());
        final PaymentData paymentData = generatePaymentQrCodeUseCase.generatePaymentQrCode(paymentDataRequest.toDto());
        final PaymentDataResponse paymentStatusResponse = PaymentDataMapper.toResponse(paymentData);

        return ResponseEntity.ok(paymentStatusResponse);
    }
    
}
