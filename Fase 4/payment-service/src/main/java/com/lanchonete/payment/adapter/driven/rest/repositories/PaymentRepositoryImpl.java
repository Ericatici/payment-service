package com.lanchonete.payment.adapter.driven.rest.repositories;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.lanchonete.payment.adapter.driven.rest.mercadopago.MercadoPagoGatewayRepository;
import com.lanchonete.payment.adapter.driven.rest.response.MPPaymentConfirmationResponse;
import com.lanchonete.payment.adapter.driven.rest.response.MPQrCodePaymentResponse;
import com.lanchonete.payment.core.domain.model.PaymentConfirmation;
import com.lanchonete.payment.core.domain.model.PaymentData;
import com.lanchonete.payment.core.domain.repositories.PaymentRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final MercadoPagoGatewayRepository mercadoPagoGateway;

    @Override
    public PaymentConfirmation getPaymentStatus(final String paymentId) {
        final MPPaymentConfirmationResponse response = mercadoPagoGateway.getPaymentConfirmation(paymentId);

        PaymentConfirmation payment = null;
        if (response != null) {
            payment =  response.toPaymentConfirmation();
        } 

        return payment;
    }

    @Override
    public PaymentData getPaymentData(final Long orderId, final BigDecimal totalPrice) {
        final MPQrCodePaymentResponse response = mercadoPagoGateway.createQrCodeForPayment(orderId, totalPrice);

        PaymentData paymentData = null;
        if (response != null) {
            paymentData =  response.toPaymentData();
        } 

        return paymentData;
    }
    
}

