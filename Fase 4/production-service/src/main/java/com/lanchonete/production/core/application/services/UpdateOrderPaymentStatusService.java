package com.lanchonete.production.core.application.services;

import org.springframework.stereotype.Service;

import static com.lanchonete.production.core.application.config.Constants.PROCESSED_STATUS;
import static com.lanchonete.production.core.application.config.Constants.REFUNDED_STATUS;
import static com.lanchonete.production.core.application.config.Constants.EXPIRED_STATUS;
import static com.lanchonete.production.core.application.config.Constants.CANCELED_STATUS;

import com.lanchonete.production.core.application.dto.PaymentConfirmationDTO;
import com.lanchonete.production.core.application.usecases.UpdateOrderPaymentStatusUseCase;
import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.production.core.domain.model.enums.PaymentStatusEnum;
import com.lanchonete.production.core.domain.repositories.OrderRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class UpdateOrderPaymentStatusService implements UpdateOrderPaymentStatusUseCase {

    private final OrderRepository ordersRepository;

    @Override
    public void updateOrderPaymentStatus(final Long orderId, final PaymentConfirmationDTO payment) {
        Order order = ordersRepository.findOrderById(orderId);
        
        if (payment !=null && payment.getStatus() != null){
            PaymentStatusEnum newPaymentStatus;
            switch (payment.getStatus().toLowerCase()) {
                case PROCESSED_STATUS:
                    newPaymentStatus = PaymentStatusEnum.APPROVED;
                    order.setStatus(OrderStatusEnum.READY); 
                    log.info("Order {} status updated to IN_PREPARATION due to payment approval.", order.getId());
                    break;
                case REFUNDED_STATUS:
                case EXPIRED_STATUS:
                case CANCELED_STATUS:
                    newPaymentStatus = PaymentStatusEnum.REJECTED;
                    order.setStatus(OrderStatusEnum.CANCELLED);
                    log.warn("Order {} status updated to CANCELLED due to payment rejection.", order.getId());
                    break;
                default:
                    newPaymentStatus = PaymentStatusEnum.PENDING; 
                    log.warn("Unknown payment status received for order {}: {}", order.getId(), payment.getStatus());
                    break;
            }

            if (order.getPaymentStatus() != newPaymentStatus) {
                order.setPaymentStatus(newPaymentStatus);
                ordersRepository.updateOrder(order);
                log.info("Payment status for order {} updated to {}.", order.getId(), newPaymentStatus);
            }

        }
    } 
}
