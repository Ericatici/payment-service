package com.lanchonete.order.core.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lanchonete.order.core.domain.exceptions.InvalidOrderException;
import com.lanchonete.order.core.domain.model.Order;
import com.lanchonete.order.core.domain.model.Product;
import com.lanchonete.order.core.domain.model.enums.ProductCategoryEnum;
import com.lanchonete.order.core.domain.repositories.OrderRepository;
import com.lanchonete.order.mocks.OrderMock;

@ExtendWith(MockitoExtension.class)
class CreateOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private FindProductService findProductService;

    @InjectMocks
    private CreateOrderService createOrderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = OrderMock.createOrderMock();
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        when(orderRepository.saveOrder(any(Order.class))).thenReturn(order);
        when(findProductService.findProductById(anyString())).thenReturn(Product.builder()
                .id("PROD-001")
                .name("Hamburger")
                .description("Delicious hamburger")
                .price(new BigDecimal("25.00"))
                .category(ProductCategoryEnum.LANCHE)
                .createdDate(Instant.now())
                .updatedDate(Instant.now())
                .build());


        Order result = createOrderService.create(order);

        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        assertEquals(order.getCustomerCpf(), result.getCustomerCpf());
        assertEquals(order.getTotalPrice(), result.getTotalPrice());
        
        verify(orderRepository, times(1)).saveOrder(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenOrderHasNoItems() {
        Order orderWithoutItems = OrderMock.createOrderWithoutItemsMock();

        assertThrows(InvalidOrderException.class, () -> {
            createOrderService.create(orderWithoutItems);
        });

        verify(orderRepository, never()).saveOrder(any(Order.class));
    }
}


