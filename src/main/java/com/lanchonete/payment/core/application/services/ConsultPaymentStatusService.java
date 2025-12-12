package com.lanchonete.payment.core.application.services;

import com.lanchonete.payment.core.application.usecases.ConsultPaymentStatusUseCase;
import com.lanchonete.payment.core.domain.exceptions.OrderNotFoundException;
import com.lanchonete.payment.core.domain.model.PaymentConfirmation;
import com.lanchonete.payment.core.domain.model.PaymentStatus;
import com.lanchonete.payment.core.domain.model.enums.PaymentStatusEnum;
import com.lanchonete.payment.core.domain.repositories.OrderRepository;
import com.lanchonete.payment.core.domain.repositories.PaymentRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ConsultPaymentStatusService implements ConsultPaymentStatusUseCase {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentStatus getPaymentStatus(final Long orderId) {
        OrderRepository.Order order = orderRepository.getOrderById(orderId);

        if (order != null) {
            log.info("Processing payment confirmation for Order: {}", orderId);

            if (order.getPaymentStatus() != PaymentStatusEnum.PENDING) {
                log.info("Order {} in final payment status: {}", orderId, order.getPaymentStatus());

                return PaymentStatus.builder()
                    .orderId(orderId)
                    .paymentStatus(order.getPaymentStatus())
                    .build();
            }

            final PaymentConfirmation payment = paymentRepository.getPaymentStatus(order.getPaymentId());
            orderRepository.updateOrderPaymentStatus(order.getId(), payment);

            order = orderRepository.getOrderById(orderId);

            return PaymentStatus.builder()
                    .orderId(orderId)
                    .paymentStatus(order.getPaymentStatus())
                    .build();
        } else {
            log.error("Order not found with id: {}", orderId);
            throw new OrderNotFoundException("Order with id " + orderId + " not found"); 
        }
    }
}

