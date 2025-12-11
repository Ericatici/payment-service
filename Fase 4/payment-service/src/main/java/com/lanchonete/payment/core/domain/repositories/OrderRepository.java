package com.lanchonete.payment.core.domain.repositories;

import com.lanchonete.payment.core.domain.model.PaymentConfirmation;
import com.lanchonete.payment.core.domain.model.enums.PaymentStatusEnum;

public interface OrderRepository {
    Order getOrderById(Long orderId);
    Order getOrderByPaymentId(String paymentId);
    void updateOrderPaymentStatus(Long orderId, PaymentConfirmation paymentConfirmation);
    
    class Order {
        private Long id;
        private String customerCpf;
        private String paymentId;
        private PaymentStatusEnum paymentStatus;
        
        public Order(Long id, String customerCpf, String paymentId, PaymentStatusEnum paymentStatus) {
            this.id = id;
            this.customerCpf = customerCpf;
            this.paymentId = paymentId;
            this.paymentStatus = paymentStatus;
        }
        
        public Long getId() {
            return id;
        }
        
        public String getCustomerCpf() {
            return customerCpf;
        }
        
        public String getPaymentId() {
            return paymentId;
        }
        
        public PaymentStatusEnum getPaymentStatus() {
            return paymentStatus;
        }
    }
}

