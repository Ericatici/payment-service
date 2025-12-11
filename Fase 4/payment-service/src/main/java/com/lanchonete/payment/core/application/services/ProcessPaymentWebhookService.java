package com.lanchonete.payment.core.application.services;

import com.lanchonete.payment.core.application.dto.PaymentConfirmationDTO;
import com.lanchonete.payment.core.application.usecases.ProcessPaymentWebhookUseCase;
import com.lanchonete.payment.core.domain.exceptions.InvalidPaymentException;
import com.lanchonete.payment.core.domain.exceptions.OrderNotFoundException;
import com.lanchonete.payment.core.domain.model.PaymentConfirmation;
import com.lanchonete.payment.core.domain.repositories.OrderRepository;
import com.lanchonete.payment.core.domain.repositories.PaymentRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
@Slf4j
@Service
@AllArgsConstructor
public class ProcessPaymentWebhookService implements ProcessPaymentWebhookUseCase {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public void processPaymentConfirmation(final PaymentConfirmationDTO paymentConfirmationDTO) {
        final String paymentId = paymentConfirmationDTO.getData().getId();
   
        final OrderRepository.Order order = orderRepository.getOrderByPaymentId(paymentId);

        if (order != null) {
            log.info("Processing payment confirmation for payment ID: {}", paymentId);

            final PaymentConfirmation payment = paymentRepository.getPaymentStatus(paymentId);

            if (payment != null) {
                orderRepository.updateOrderPaymentStatus(order.getId(), payment);    
            } else {
                log.error("No payment confirmation received for paymentId {}", paymentId);
                throw new InvalidPaymentException("No payment confirmation received for paymentId " + paymentId);
            }
        } else {
            log.error("No order with paymentId {} found in the database", paymentId);
            throw new OrderNotFoundException("Order with paymentId " + paymentId + " not found"); 
        }
    }  
}

