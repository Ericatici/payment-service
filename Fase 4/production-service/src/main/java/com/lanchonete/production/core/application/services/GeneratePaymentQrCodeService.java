package com.lanchonete.production.core.application.services;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Service;

import com.lanchonete.production.core.application.dto.PaymentDataDTO;
import com.lanchonete.production.core.application.ports.PaymentServicePort;
import com.lanchonete.production.core.application.usecases.GeneratePaymentQrCodeUseCase;
import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.production.core.domain.model.enums.PaymentStatusEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class GeneratePaymentQrCodeService implements GeneratePaymentQrCodeUseCase {

    private final PaymentServicePort paymentServicePort;

    @Override
    public Order generatePaymentQrCode(Order order) {
        log.info("Getting payment data information");
        final PaymentDataDTO paymentData = paymentServicePort.getPaymentData(order, ThreadContext.get("requestTraceId"));
        
        if (paymentData != null && paymentData.getPaymentId() != null && paymentData.getQrCode() != null) {
            log.info("Updating order with payment data information");
            order.setPaymentId(paymentData.getPaymentId());
            order.setQrCodeData(paymentData.getQrCode());
            return order;
        }

        log.warn("Order cancelled due to invalid payment data information");
        order.setPaymentStatus(PaymentStatusEnum.REJECTED);
        order.setStatus(OrderStatusEnum.CANCELLED);
        return order;
    }
    
}
