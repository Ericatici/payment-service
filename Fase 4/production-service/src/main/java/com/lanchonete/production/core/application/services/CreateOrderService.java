package com.lanchonete.production.core.application.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.lanchonete.production.core.application.dto.CustomerDTO;
import com.lanchonete.production.core.application.ports.CustomerServicePort;
import com.lanchonete.production.core.application.usecases.CreateOrderItemUseCase;
import com.lanchonete.production.core.application.usecases.GeneratePaymentQrCodeUseCase;
import com.lanchonete.production.core.application.usecases.CreateOrderUseCase;
import com.lanchonete.production.core.domain.exceptions.InvalidOrderException;
import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.OrderItem;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.production.core.domain.model.enums.PaymentStatusEnum;
import com.lanchonete.production.core.domain.repositories.OrderRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final CustomerServicePort customerServicePort;
    private final CreateOrderItemUseCase createOrderItemUseCase;
    private final GeneratePaymentQrCodeUseCase generatePaymentQrCodeUseCase;

    @Override
    public Order create(final Order order) {
      
        CustomerDTO customer = new CustomerDTO();

        validateCustomer(order.getCustomerCpf(), customer);

        validateOrder(order);

        if (CollectionUtils.isEmpty(order.getItems())){
            throw new InvalidOrderException("Order with no items, order is invalid");
        }
   
        Order orderData = Order.builder()
                .customerCpf(customer.getCpf())
                .customerName(customer.getName())
                .orderDate(LocalDateTime.now())
                .status(OrderStatusEnum.WAITING_PAYMENT)
                .paymentStatus(PaymentStatusEnum.PENDING)
                .items(new ArrayList<>())
                .build();
        
        log.info("Customer validated 2: {}", customer.toString());
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            OrderItem orderItem = createOrderItemUseCase.createOrderItem(item);
            orderData.getItems().add(orderItem);
            totalPrice = totalPrice.add(orderItem.getItemsTotalPrice()); 
        }

        orderData.setTotalPrice(totalPrice);

        final Order savedOrder = orderRepository.saveOrder(orderData);

        final Order paymentUpdatedOrder = generatePaymentQrCodeUseCase.generatePaymentQrCode(savedOrder);

        final Order updatedOrder = orderRepository.updateOrder(paymentUpdatedOrder);

        log.info("Created order with id: {}", updatedOrder.getId());

        return updatedOrder;
    }

    private void validateOrder(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            log.error("Cannot create order without items");
            throw new InvalidOrderException("Order must have at least one item");
        }
    }

    private void validateCustomer(String customerCpf, CustomerDTO customer) {
        if (StringUtils.isNotBlank(customerCpf)) {
            log.debug("Validating customer with CPF: {}", customerCpf);
        
            CustomerDTO customerDTO = customerServicePort.getCustomerByCpf(customerCpf);

            if (customerDTO == null) {
                log.error("Customer not found with CPF: {}", customerCpf);
                throw new InvalidOrderException("Customer with CPF " + customerCpf + " not found");
            }

            customer.setCpf(customerCpf);
            customer.setEmail(customerDTO.getEmail());
            customer.setName(customerDTO.getName());
            
            log.info("Customer validated: {}", customer.getName());
        }
        
    }

}

