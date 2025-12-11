package com.lanchonete.order.core.application.services;

import org.springframework.stereotype.Service;

import com.lanchonete.order.core.application.usecases.CreateOrderUseCase;
import com.lanchonete.order.core.application.usecases.FindProductUseCase;
import com.lanchonete.order.core.domain.exceptions.InvalidOrderException;
import com.lanchonete.order.core.domain.model.Order;
import com.lanchonete.order.core.domain.model.OrderItem;
import com.lanchonete.order.core.domain.model.Product;
import com.lanchonete.order.core.domain.repositories.OrderRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final FindProductUseCase findProductUsecase;

    @Override
    public Order create(Order order) {
        log.info("Creating order: {}", order);
        
        if (order.getItems() == null || order.getItems().isEmpty()) {
            log.error("Cannot create order without items");
            throw new InvalidOrderException("Order must have at least one item");
        }

        validateAndEnrichOrderItems(order);
                
        Order savedOrder = orderRepository.saveOrder(order);
        
        log.info("Order created successfully with ID: {}", savedOrder.getId());
        return savedOrder;
    }


    private void validateAndEnrichOrderItems(Order order) {
        for (OrderItem item : order.getItems()) {
            if (item.getProductId() == null || item.getProductId().isEmpty()) {
                throw new InvalidOrderException("All order items must have a product ID");
            }
            
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new InvalidOrderException("All order items must have a positive quantity");
            }
            
            Product product = findProductUsecase.findProductById(item.getProductId());
            
            if (product == null) {
                log.error("Product not found with ID: {}", item.getProductId());
                throw new InvalidOrderException("Product with ID " + item.getProductId() + " not found");
            }
            
            if (item.getItemPrice() == null) {
                item.setItemPrice(product.getPrice());
            }

            if (item.getProductName() == null) {
                item.setProductName(product.getName());
            }
            
            log.debug("Product validated: {} - Price: {}", product.getName(), product.getPrice());
        }
    }
}


