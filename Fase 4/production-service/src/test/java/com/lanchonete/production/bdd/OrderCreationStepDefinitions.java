package com.lanchonete.production.bdd;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lanchonete.production.core.application.dto.CustomerDTO;
import com.lanchonete.production.core.application.dto.PaymentDataDTO;
import com.lanchonete.production.core.application.ports.CustomerServicePort;
import com.lanchonete.production.core.application.ports.PaymentServicePort;
import com.lanchonete.production.core.application.services.CreateOrderService;
import com.lanchonete.production.core.application.usecases.CreateOrderItemUseCase;
import com.lanchonete.production.core.application.usecases.GeneratePaymentQrCodeUseCase;
import com.lanchonete.production.core.domain.exceptions.InvalidOrderException;
import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.OrderItem;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.production.core.domain.model.enums.PaymentStatusEnum;
import com.lanchonete.production.core.domain.repositories.OrderRepository;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
public class OrderCreationStepDefinitions {

    private CreateOrderService createOrderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerServicePort customerServicePort;

    @Mock
    private PaymentServicePort paymentServicePort;

    @Mock
    private CreateOrderItemUseCase createOrderItemUseCase;

    @Mock
    private GeneratePaymentQrCodeUseCase generatePaymentQrCodeUseCase;

    private Order order;
    private Order createdOrder;
    private InvalidOrderException thrownException;
    private CustomerDTO customerDTO;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        createOrderService = new CreateOrderService(
            orderRepository,
            customerServicePort,
            createOrderItemUseCase,
            generatePaymentQrCodeUseCase
        );
        
        order = null;
        createdOrder = null;
        thrownException = null;
        customerDTO = null;
        
        when(paymentServicePort.getPaymentData(any(Order.class), anyString())).thenReturn(
            PaymentDataDTO.builder()
                .paymentId("PAY-12345")
                .qrCode("00020126580014br.gov.bcb.pix")
                .build()
        );
    }

    @Given("there is a customer with CPF {string}")
    public void there_is_a_customer_with_cpf(String cpf) {
        customerDTO = CustomerDTO.builder()
                .cpf(cpf)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        
        when(customerServicePort.getCustomerByCpf(cpf)).thenReturn(customerDTO);
    }

    @Given("the customer has name {string}")
    public void the_customer_has_name(String name) {
        if (customerDTO != null) {
            customerDTO.setName(name);
        }
    }

    @Given("there is no customer with CPF {string}")
    public void there_is_no_customer_with_cpf(String cpf) {
        customerDTO = null;
        when(customerServicePort.getCustomerByCpf(cpf)).thenReturn(null);
    }

    @When("I create an order with the following items:")
    public void i_create_an_order_with_the_following_items(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> items = dataTable.asMaps(String.class, String.class);
        List<OrderItem> orderItems = new ArrayList<>();

        for (Map<String, String> item : items) {
            OrderItem orderItem = OrderItem.builder()
                    .productId(item.get("productId"))
                    .productName(item.get("productName"))
                    .quantity(Integer.parseInt(item.get("quantity")))
                    .itemPrice(new BigDecimal(item.get("itemPrice")))
                    .build();
            orderItems.add(orderItem);
        }

        order = Order.builder()
                .customerCpf(customerDTO != null ? customerDTO.getCpf() : "12345678900")
                .items(orderItems)
                .build();

        try {
            when(createOrderItemUseCase.createOrderItem(any(OrderItem.class))).thenAnswer(invocation -> {
                OrderItem item = invocation.getArgument(0);
                return OrderItem.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .quantity(item.getQuantity())
                    .itemPrice(item.getItemPrice())
                    .itemsTotalPrice(item.getItemPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .build();
            });
            
            Order savedOrder = Order.builder()
                .id(1L)
                .customerCpf(order.getCustomerCpf())
                .customerName(customerDTO != null ? customerDTO.getName() : "Test Customer")
                .items(order.getItems())
                .status(OrderStatusEnum.WAITING_PAYMENT)
                .paymentStatus(PaymentStatusEnum.PENDING)
                .build();
            
            when(orderRepository.saveOrder(any(Order.class))).thenReturn(savedOrder);
            
            Order paymentUpdatedOrder = Order.builder()
                .id(1L)
                .customerCpf(order.getCustomerCpf())
                .customerName(customerDTO != null ? customerDTO.getName() : "Test Customer")
                .items(order.getItems())
                .status(OrderStatusEnum.WAITING_PAYMENT)
                .paymentStatus(PaymentStatusEnum.PENDING)
                .paymentId("PAY-12345")
                .qrCodeData("00020126580014br.gov.bcb.pix")
                .build();
            
            when(generatePaymentQrCodeUseCase.generatePaymentQrCode(any(Order.class))).thenReturn(paymentUpdatedOrder);
            when(orderRepository.updateOrder(any(Order.class))).thenReturn(paymentUpdatedOrder);
            
            createdOrder = createOrderService.create(order);
        } catch (InvalidOrderException e) {
            thrownException = e;
        }
    }

    @When("I try to create an order with CPF {string}")
    public void i_try_to_create_an_order_with_cpf(String cpf) {
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = OrderItem.builder()
                .productId("PROD-001")
                .productName("Test Product")
                .quantity(1)
                .itemPrice(new BigDecimal("10.00"))
                .build();
        orderItems.add(orderItem);

        order = Order.builder()
                .customerCpf(cpf)
                .items(orderItems)
                .build();

        try {
            when(createOrderItemUseCase.createOrderItem(any(OrderItem.class))).thenAnswer(invocation -> {
                OrderItem item = invocation.getArgument(0);
                return OrderItem.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .quantity(item.getQuantity())
                    .itemPrice(item.getItemPrice())
                    .itemsTotalPrice(item.getItemPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .build();
            });
            
            createdOrder = createOrderService.create(order);
        } catch (InvalidOrderException e) {
            thrownException = e;
        }
    }

    @Then("the order should be created successfully")
    public void the_order_should_be_created_successfully() {
        assertNotNull(createdOrder, "The order should have been created");
        assertNotNull(createdOrder.getId(), "The order should have an ID");
    }

    @Then("the order should have status {string}")
    public void the_order_should_have_status(String status) {
        assertNotNull(createdOrder, "The order should have been created");
        OrderStatusEnum expectedStatus = OrderStatusEnum.valueOf(status);
        assertEquals(expectedStatus, createdOrder.getStatus(), 
                "The order status should be " + status);
    }

    @Then("the order should have paymentStatus {string}")
    public void the_order_should_have_payment_status(String paymentStatus) {
        assertNotNull(createdOrder, "The order should have been created");
        PaymentStatusEnum expectedPaymentStatus = PaymentStatusEnum.valueOf(paymentStatus);
        assertEquals(expectedPaymentStatus, createdOrder.getPaymentStatus(), 
                "The order paymentStatus should be " + paymentStatus);
    }

    @Then("the order should have a paymentId")
    public void the_order_should_have_a_payment_id() {
        assertNotNull(createdOrder, "The order should have been created");
        assertNotNull(createdOrder.getPaymentId(), "The order should have a paymentId");
        assertFalse(createdOrder.getPaymentId().isEmpty(), "The paymentId should not be empty");
    }

    @Then("the order should have a qrCodeData")
    public void the_order_should_have_a_qr_code_data() {
        assertNotNull(createdOrder, "The order should have been created");
        assertNotNull(createdOrder.getQrCodeData(), "The order should have a qrCodeData");
        assertFalse(createdOrder.getQrCodeData().isEmpty(), "The qrCodeData should not be empty");
    }

    @Then("an InvalidOrderException should be thrown")
    public void an_invalid_order_exception_should_be_thrown() {
        assertNotNull(thrownException, "An InvalidOrderException should have been thrown");
    }

    @Then("the error message should contain {string}")
    public void the_error_message_should_contain(String expectedMessage) {
        assertNotNull(thrownException, "An exception should have been thrown");
        assertTrue(thrownException.getMessage().contains(expectedMessage), 
                "The error message should contain: " + expectedMessage);
    }
}
