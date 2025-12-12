package com.lanchonete.payment.bdd;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.lanchonete.payment.core.application.services.ConsultPaymentStatusService;
import com.lanchonete.payment.core.domain.exceptions.OrderNotFoundException;
import com.lanchonete.payment.core.domain.model.PaymentConfirmation;
import com.lanchonete.payment.core.domain.model.PaymentStatus;
import com.lanchonete.payment.core.domain.model.enums.PaymentStatusEnum;
import com.lanchonete.payment.core.domain.repositories.OrderRepository;
import com.lanchonete.payment.core.domain.repositories.PaymentRepository;
import com.lanchonete.payment.mocks.PaymentConfirmationMock;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(classes = com.lanchonete.payment.PaymentServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
    "production.service.url=http://localhost:8082",
    "mercadopago.baseUrl=https://api.mercadopago.com",
    "mercadopago.externalPosId=test-pos",
    "mercadopago.access.client=test-client",
    "mercadopago.access.secret=test-secret"
})
public class PaymentStatusStepDefinitions {

    @Autowired
    private ConsultPaymentStatusService consultPaymentStatusService;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private PaymentRepository paymentRepository;

    private PaymentStatus paymentStatusResult;
    private Exception thrownException;
    private Long currentOrderId;
    private String currentPaymentId;

    @Before
    public void setUp() {
        reset(orderRepository, paymentRepository);
        paymentStatusResult = null;
        thrownException = null;
        currentOrderId = null;
        currentPaymentId = null;
    }

    @Given("an order with id {string} exists")
    public void anOrderWithIdExists(String orderId) {
        this.currentOrderId = Long.parseLong(orderId);
    }

    @Given("an order with id {string} does not exist")
    public void anOrderWithIdDoesNotExist(String orderId) {
        this.currentOrderId = Long.parseLong(orderId);
        when(orderRepository.getOrderById(anyLong())).thenReturn(null);
    }

    @And("the order has payment status {string}")
    public void theOrderHasPaymentStatus(String status) {
        PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.valueOf(status);
        OrderRepository.Order order = new OrderRepository.Order(
            currentOrderId,
            "12345678900",
            "mp-payment-123",
            paymentStatusEnum
        );
        when(orderRepository.getOrderById(currentOrderId)).thenReturn(order);
    }

    @And("the order has payment id {string}")
    public void theOrderHasPaymentId(String paymentId) {
        this.currentPaymentId = paymentId;
        OrderRepository.Order order = new OrderRepository.Order(
            currentOrderId,
            "12345678900",
            paymentId,
            PaymentStatusEnum.PENDING
        );
        when(orderRepository.getOrderById(currentOrderId)).thenReturn(order);
    }

    @And("MercadoPago returns an approved payment for {string}")
    public void mercadoPagoReturnsAnApprovedPayment(String paymentId) {
        PaymentConfirmation paymentConfirmation = PaymentConfirmationMock.createApprovedPaymentMock();
        when(paymentRepository.getPaymentStatus(paymentId)).thenReturn(paymentConfirmation);
        
        OrderRepository.Order pendingOrder = new OrderRepository.Order(
            currentOrderId,
            "12345678900",
            paymentId,
            PaymentStatusEnum.PENDING
        );
        OrderRepository.Order approvedOrder = new OrderRepository.Order(
            currentOrderId,
            "12345678900",
            paymentId,
            PaymentStatusEnum.APPROVED
        );
        when(orderRepository.getOrderById(currentOrderId)).thenReturn(pendingOrder, approvedOrder);
    }

    @When("I consult the payment status for order {string}")
    public void iConsultThePaymentStatusForOrder(String orderId) {
        try {
            paymentStatusResult = consultPaymentStatusService.getPaymentStatus(Long.parseLong(orderId));
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the payment status should be {string}")
    public void thePaymentStatusShouldBe(String expectedStatus) {
        assertNotNull(paymentStatusResult, "Payment status result should not be null");
        PaymentStatusEnum expected = PaymentStatusEnum.valueOf(expectedStatus);
        assertEquals(expected, paymentStatusResult.getPaymentStatus(),
            "Payment status should be " + expectedStatus);
    }

    @And("the MercadoPago API should not be consulted")
    public void theMercadoPagoAPIShouldNotBeConsulted() {
        verify(paymentRepository, never()).getPaymentStatus(anyString());
    }

    @And("the MercadoPago API should be consulted once")
    public void theMercadoPagoAPIShouldBeConsultedOnce() {
        verify(paymentRepository, times(1)).getPaymentStatus(anyString());
    }

    @And("the order payment status should be updated")
    public void theOrderPaymentStatusShouldBeUpdated() {
        verify(orderRepository, atLeastOnce()).updateOrderPaymentStatus(anyLong(), any(PaymentConfirmation.class));
    }

    @Then("an OrderNotFoundException should be thrown")
    public void anOrderNotFoundExceptionShouldBeThrown() {
        assertNotNull(thrownException, "Exception should be thrown");
        assertTrue(thrownException instanceof OrderNotFoundException,
            "Exception should be OrderNotFoundException");
    }

    @And("the error message should contain {string}")
    public void theErrorMessageShouldContain(String expectedMessage) {
        assertNotNull(thrownException, "Exception should be thrown");
        assertTrue(thrownException.getMessage().contains(expectedMessage),
            "Error message should contain: " + expectedMessage);
    }
}

