package com.lanchonete.payment.core.application.services;

import org.springframework.stereotype.Service;

import com.lanchonete.payment.core.application.dto.PaymentDataDTO;
import com.lanchonete.payment.core.application.usecases.GeneratePaymentQrCodeUseCase;
import com.lanchonete.payment.core.domain.model.PaymentData;
import com.lanchonete.payment.core.domain.repositories.PaymentRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class GeneratePaymentQrCodeService implements GeneratePaymentQrCodeUseCase {

    private final PaymentRepository paymentRepository;

    @Override
    public PaymentData generatePaymentQrCode(PaymentDataDTO paymentDataDTO) {
        log.info("Getting payment data information");
        return paymentRepository.getPaymentData(paymentDataDTO.getOrderId(), paymentDataDTO.getTotalPrice());
    }
    
}

