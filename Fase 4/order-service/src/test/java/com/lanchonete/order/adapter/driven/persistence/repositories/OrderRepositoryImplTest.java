package com.lanchonete.order.adapter.driven.persistence.repositories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lanchonete.order.adapter.driven.clients.ProductionServiceClient;
import com.lanchonete.order.core.application.dto.OrderDTO;
import com.lanchonete.order.core.domain.model.Order;
import com.lanchonete.order.mocks.OrderMock;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryImplTest {

    @Mock
    private ProductionServiceClient productionServiceClient;

    @InjectMocks
    private OrderRepositoryImpl orderRepository;

    private Order order;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        order = OrderMock.createOrderMock();
        
        orderDTO = OrderDTO.builder()
                .id(order.getId())
                .customerCpf(order.getCustomerCpf())
                .customerName(order.getCustomerName())
                .items(order.getItems().stream()
                        .map(item -> OrderDTO.OrderItemDTO.builder()
                                .productId(item.getProductId())
                                .productName(item.getProductName())
                                .quantity(item.getQuantity())
                                .itemPrice(item.getItemPrice())
                                .itemsTotalPrice(item.getItemsTotalPrice())
                                .build())
                        .toList())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .qrCodeData(order.getQrCodeData())
                .paymentId(order.getPaymentId())
                .totalPrice(order.getTotalPrice())
                .createdDate(order.getCreatedDate())
                .updatedDate(order.getUpdatedDate())
                .build();
    }

    @Test
    void shouldSaveOrderSuccessfully() {
        when(productionServiceClient.createOrder(any(OrderDTO.class), anyString()))
                .thenReturn(orderDTO);

        Order result = orderRepository.saveOrder(order);

        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        assertEquals(order.getCustomerCpf(), result.getCustomerCpf());
        assertEquals(order.getTotalPrice(), result.getTotalPrice());
        
        verify(productionServiceClient, times(1)).createOrder(any(OrderDTO.class), anyString());
    }

    @Test
    void shouldFindOrderByIdSuccessfully() {
        when(productionServiceClient.getOrderById(eq(order.getId()), anyString()))
                .thenReturn(orderDTO);

        Optional<Order> result = orderRepository.findById(order.getId());

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(order.getId(), result.get().getId());
        assertEquals(order.getCustomerCpf(), result.get().getCustomerCpf());
        
        verify(productionServiceClient, times(1)).getOrderById(eq(order.getId()), anyString());
    }

    @Test
    void shouldReturnEmptyOptionalWhenOrderNotFound() {
        Long orderId = 999L;
        when(productionServiceClient.getOrderById(eq(orderId), anyString()))
                .thenReturn(null);

        Optional<Order> result = orderRepository.findById(orderId);

        assertTrue(result.isEmpty());
        
        verify(productionServiceClient, times(1)).getOrderById(eq(orderId), anyString());
    }

    @Test
    void shouldFindAllOrdersSuccessfully() {
        List<OrderDTO> orderDTOs = Arrays.asList(orderDTO);
        when(productionServiceClient.getAllOrders(anyString()))
                .thenReturn(orderDTOs);

        List<Order> result = orderRepository.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(order.getId(), result.get(0).getId());
        
        verify(productionServiceClient, times(1)).getAllOrders(anyString());
    }

    @Test
    void shouldReturnEmptyListWhenNoOrdersFound() {
        when(productionServiceClient.getAllOrders(anyString()))
                .thenReturn(Collections.emptyList());

        List<Order> result = orderRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(productionServiceClient, times(1)).getAllOrders(anyString());
    }

    @Test
    void shouldFindActiveOrdersSuccessfully() {
        List<OrderDTO> orderDTOs = Arrays.asList(orderDTO);
        when(productionServiceClient.getActiveOrders(anyString()))
                .thenReturn(orderDTOs);

        List<Order> result = orderRepository.findActiveOrders();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(order.getId(), result.get(0).getId());
        
        verify(productionServiceClient, times(1)).getActiveOrders(anyString());
    }

    @Test
    void shouldThrowExceptionWhenDeletingOrder() {
        Long orderId = 1L;

        assertThrows(UnsupportedOperationException.class, () -> {
            orderRepository.deleteById(orderId);
        });
    }

    @Test
    void shouldConvertOrderToDTOWithNullItems() {
        Order orderWithoutItems = Order.builder()
                .id(1L)
                .customerCpf("12345678900")
                .items(null)
                .build();
        
        when(productionServiceClient.createOrder(any(OrderDTO.class), anyString()))
                .thenAnswer(invocation -> {
                    OrderDTO dto = invocation.getArgument(0);
                    assertNull(dto.getItems());
                    return orderDTO;
                });

        orderRepository.saveOrder(orderWithoutItems);

        verify(productionServiceClient, times(1)).createOrder(any(OrderDTO.class), anyString());
    }

    @Test
    void shouldConvertDTOToOrderWithNullItems() {
        OrderDTO dtoWithoutItems = OrderDTO.builder()
                .id(1L)
                .customerCpf("12345678900")
                .items(null)
                .build();
        
        when(productionServiceClient.getOrderById(eq(1L), anyString()))
                .thenReturn(dtoWithoutItems);

        Optional<Order> result = orderRepository.findById(1L);

        assertTrue(result.isPresent());
        assertNull(result.get().getItems());
        
        verify(productionServiceClient, times(1)).getOrderById(eq(1L), anyString());
    }
}

