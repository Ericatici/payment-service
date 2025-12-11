package com.lanchonete.production.core.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lanchonete.production.core.application.dto.CustomerDTO;
import com.lanchonete.production.core.application.ports.CustomerServicePort;
import com.lanchonete.production.core.application.usecases.CreateOrderItemUseCase;
import com.lanchonete.production.core.application.usecases.GeneratePaymentQrCodeUseCase;
import com.lanchonete.production.core.domain.exceptions.InvalidOrderException;
import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.OrderItem;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.production.core.domain.model.enums.PaymentStatusEnum;
import com.lanchonete.production.core.domain.repositories.OrderRepository;
import com.lanchonete.production.mocks.CustomerDTOMock;
import com.lanchonete.production.mocks.OrderMock;

@ExtendWith(MockitoExtension.class)
class CreateOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerServicePort customerServicePort;

    @Mock
    private CreateOrderItemUseCase createOrderItemUseCase;

    @Mock
    private GeneratePaymentQrCodeUseCase generatePaymentQrCodeUseCase;

    @InjectMocks
    private CreateOrderService createOrderService;

    private Order order;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        order = OrderMock.createOrderMock();
        customerDTO = CustomerDTOMock.createCustomerDTOMock();
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        Order savedOrder = OrderMock.createOrderMock();
        savedOrder.setStatus(OrderStatusEnum.WAITING_PAYMENT);
        savedOrder.setPaymentStatus(PaymentStatusEnum.PENDING);
        
        Order paymentUpdatedOrder = OrderMock.createOrderMock();
        paymentUpdatedOrder.setPaymentId("PAY-123");
        paymentUpdatedOrder.setQrCodeData("QR-CODE-DATA");
        
        Order finalOrder = OrderMock.createOrderMock();
        finalOrder.setPaymentId("PAY-123");
        finalOrder.setQrCodeData("QR-CODE-DATA");
        
        when(customerServicePort.getCustomerByCpf(anyString())).thenReturn(customerDTO);
        when(createOrderItemUseCase.createOrderItem(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem item = invocation.getArgument(0);
            return OrderItem.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .quantity(item.getQuantity())
                    .itemPrice(item.getItemPrice())
                    .itemsTotalPrice(item.getItemPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                    .build();
        });
        when(orderRepository.saveOrder(any(Order.class))).thenReturn(savedOrder);
        when(generatePaymentQrCodeUseCase.generatePaymentQrCode(any(Order.class))).thenReturn(paymentUpdatedOrder);
        when(orderRepository.updateOrder(any(Order.class))).thenReturn(finalOrder);
        Order result = createOrderService.create(order);
        assertNotNull(result);
        assertEquals(finalOrder.getId(), result.getId());
        assertEquals(finalOrder.getCustomerCpf(), result.getCustomerCpf());
        
        verify(customerServicePort, times(1)).getCustomerByCpf(anyString());
        verify(createOrderItemUseCase, atLeastOnce()).createOrderItem(any(OrderItem.class));
        verify(orderRepository, times(1)).saveOrder(any(Order.class));
        verify(generatePaymentQrCodeUseCase, times(1)).generatePaymentQrCode(any(Order.class));
        verify(orderRepository, times(1)).updateOrder(any(Order.class));
    }

    @Test
    void shouldSetDefaultStatusWhenNotProvided() {
        Order orderWithoutStatus = OrderMock.createOrderMock();
        orderWithoutStatus.setStatus(null);
        orderWithoutStatus.setPaymentStatus(null);
        
        Order savedOrder = OrderMock.createOrderMock();
        savedOrder.setStatus(OrderStatusEnum.WAITING_PAYMENT);
        savedOrder.setPaymentStatus(PaymentStatusEnum.PENDING);
        
        Order paymentUpdatedOrder = OrderMock.createOrderMock();
        paymentUpdatedOrder.setPaymentId("PAY-123");
        paymentUpdatedOrder.setQrCodeData("QR-CODE-DATA");
        
        Order finalOrder = OrderMock.createOrderMock();
        finalOrder.setPaymentId("PAY-123");
        finalOrder.setQrCodeData("QR-CODE-DATA");
        
        when(customerServicePort.getCustomerByCpf(anyString())).thenReturn(customerDTO);
        when(createOrderItemUseCase.createOrderItem(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem item = invocation.getArgument(0);
            return OrderItem.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .quantity(item.getQuantity())
                    .itemPrice(item.getItemPrice())
                    .itemsTotalPrice(item.getItemPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                    .build();
        });
        when(orderRepository.saveOrder(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            assertEquals(OrderStatusEnum.WAITING_PAYMENT, o.getStatus());
            assertEquals(PaymentStatusEnum.PENDING, o.getPaymentStatus());
            return savedOrder;
        });
        when(generatePaymentQrCodeUseCase.generatePaymentQrCode(any(Order.class))).thenReturn(paymentUpdatedOrder);
        when(orderRepository.updateOrder(any(Order.class))).thenReturn(finalOrder);
        Order result = createOrderService.create(orderWithoutStatus);
        assertNotNull(result);
        verify(orderRepository, times(1)).saveOrder(any(Order.class));
    }

    @Test
    void shouldCalculateTotalPrice() {
        Order orderWithoutTotal = OrderMock.createOrderMock();
        orderWithoutTotal.setTotalPrice(null);
        
        Order savedOrder = OrderMock.createOrderMock();
        savedOrder.setStatus(OrderStatusEnum.WAITING_PAYMENT);
        savedOrder.setPaymentStatus(PaymentStatusEnum.PENDING);
        
        Order paymentUpdatedOrder = OrderMock.createOrderMock();
        paymentUpdatedOrder.setPaymentId("PAY-123");
        paymentUpdatedOrder.setQrCodeData("QR-CODE-DATA");
        
        Order finalOrder = OrderMock.createOrderMock();
        finalOrder.setPaymentId("PAY-123");
        finalOrder.setQrCodeData("QR-CODE-DATA");
        
        when(customerServicePort.getCustomerByCpf(anyString())).thenReturn(customerDTO);
        when(createOrderItemUseCase.createOrderItem(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem item = invocation.getArgument(0);
            return OrderItem.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .quantity(item.getQuantity())
                    .itemPrice(item.getItemPrice())
                    .itemsTotalPrice(item.getItemPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                    .build();
        });
        when(orderRepository.saveOrder(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            assertNotNull(o.getTotalPrice());
            assertTrue(o.getTotalPrice().compareTo(java.math.BigDecimal.ZERO) > 0);
            return savedOrder;
        });
        when(generatePaymentQrCodeUseCase.generatePaymentQrCode(any(Order.class))).thenReturn(paymentUpdatedOrder);
        when(orderRepository.updateOrder(any(Order.class))).thenReturn(finalOrder);
        Order result = createOrderService.create(orderWithoutTotal);
        assertNotNull(result);
        verify(orderRepository, times(1)).saveOrder(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenOrderHasNoItems() {
        Order orderWithoutItems = OrderMock.createOrderMock();
        orderWithoutItems.setItems(null);
        assertThrows(InvalidOrderException.class, () -> {
            createOrderService.create(orderWithoutItems);
        });

        verify(orderRepository, never()).saveOrder(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        when(customerServicePort.getCustomerByCpf(anyString())).thenReturn(null);
        assertThrows(InvalidOrderException.class, () -> {
            createOrderService.create(order);
        });

        verify(orderRepository, never()).saveOrder(any(Order.class));
    }

}

