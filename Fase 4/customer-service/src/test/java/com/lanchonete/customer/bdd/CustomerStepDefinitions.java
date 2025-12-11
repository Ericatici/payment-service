package com.lanchonete.customer.bdd;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lanchonete.customer.adapter.driver.rest.requests.CustomerRequest;
import com.lanchonete.customer.adapter.driver.rest.requests.CustomerUpdateRequest;
import com.lanchonete.customer.core.application.usecases.CreateCustomerUseCase;
import com.lanchonete.customer.core.application.usecases.FindCustomerUseCase;
import com.lanchonete.customer.core.application.usecases.UpdateCustomerUseCase;
import com.lanchonete.customer.core.domain.exceptions.CustomerAlreadyExistsException;
import com.lanchonete.customer.core.domain.model.Customer;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CustomerStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateCustomerUseCase createCustomerUseCase;

    @MockitoBean
    private FindCustomerUseCase findCustomerUseCase;

    @MockitoBean
    private UpdateCustomerUseCase updateCustomerUseCase;

    private MvcResult result;
    private CustomerRequest customerRequest;
    private Customer customer;

    @Given("I have a valid customer with CPF {string}, name {string} and email {string}")
    public void i_have_a_valid_customer_with_cpf_name_and_email(String cpf, String name, String email) {
        customerRequest = CustomerRequest.builder()
                .cpf(cpf)
                .name(name)
                .email(email)
                .build();
        
        customer = Customer.builder()
                .cpf(cpf)
                .name(name)
                .email(email)
                .build();
    }

    @When("I create the customer")
    public void i_create_the_customer() throws Exception {
        org.mockito.Mockito.when(createCustomerUseCase.createCustomer(org.mockito.ArgumentMatchers.any())).thenReturn(customer);
        
        result = mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andReturn();
    }

    @Then("the customer should be created successfully with status {int}")
    public void the_customer_should_be_created_successfully_with_status(Integer status) {
        assertEquals(status.intValue(), result.getResponse().getStatus());
    }

    @Then("the response should contain CPF {string}, name {string} and email {string}")
    public void the_response_should_contain_cpf_name_and_email(String cpf, String name, String email) throws Exception {
        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains(cpf));
        assertTrue(responseContent.contains(name));
        assertTrue(responseContent.contains(email));
    }

    @Given("a customer with CPF {string} already exists")
    public void a_customer_with_cpf_already_exists(String cpf) {
        Customer existingCustomer = Customer.builder()
                .cpf(cpf)
                .name("Existing Customer")
                .email("existing@example.com")
                .build();
        
        org.mockito.Mockito.when(findCustomerUseCase.getCustomerOptionalByCpf(cpf))
                .thenReturn(java.util.Optional.of(existingCustomer));
        org.mockito.Mockito.when(createCustomerUseCase.createCustomer(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new CustomerAlreadyExistsException("Customer with CPF " + cpf + " already exists"));
    }

    @When("I try to create a customer with CPF {string}, name {string} and email {string}")
    public void i_try_to_create_a_customer_with_cpf_name_and_email(String cpf, String name, String email) throws Exception {
        customerRequest = CustomerRequest.builder()
                .cpf(cpf)
                .name(name)
                .email(email)
                .build();
        
        result = mockMvc.perform(post("/customer/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andReturn();
    }

    @Then("I should receive an error with status {int}")
    public void i_should_receive_an_error_with_status(Integer status) {
        assertEquals(status.intValue(), result.getResponse().getStatus());
    }

    @Then("the error message should indicate that the customer already exists")
    public void the_error_message_should_indicate_that_the_customer_already_exists() throws Exception {
        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains("already exists"));
    }

    @Given("a customer with CPF {string} exists in the system")
    public void a_customer_with_cpf_exists_in_the_system(String cpf) {
        customer = Customer.builder()
                .cpf(cpf)
                .name("Test Customer")
                .email("test@example.com")
                .build();
        
        org.mockito.Mockito.when(findCustomerUseCase.getCustomerOptionalByCpf(cpf))
                .thenReturn(java.util.Optional.of(customer));
    }

    @When("I search for the customer with CPF {string}")
    public void i_search_for_the_customer_with_cpf(String cpf) throws Exception {
        result = mockMvc.perform(get("/customer/{cpf}", cpf))
                .andReturn();
    }

    @Then("the customer should be found with status {int}")
    public void the_customer_should_be_found_with_status(Integer status) {
        assertEquals(status.intValue(), result.getResponse().getStatus());
    }

    @Then("the response should contain the customer information")
    public void the_response_should_contain_the_customer_information() throws Exception {
        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains(customer.getCpf()));
        assertTrue(responseContent.contains(customer.getName()));
    }

    @Given("no customer with CPF {string} exists in the system")
    public void no_customer_with_cpf_exists_in_the_system(String cpf) {
        org.mockito.Mockito.when(findCustomerUseCase.getCustomerOptionalByCpf(cpf))
                .thenReturn(java.util.Optional.empty());
    }

    @Then("I should receive a not found response with status {int}")
    public void i_should_receive_a_not_found_response_with_status(Integer status) {
        assertEquals(status.intValue(), result.getResponse().getStatus());
    }

    @When("I update the customer with CPF {string} with name {string} and email {string}")
    public void i_update_the_customer_with_cpf_with_name_and_email(String cpf, String name, String email) throws Exception {
        Customer updatedCustomer = Customer.builder()
                .cpf(cpf)
                .name(name)
                .email(email)
                .build();
        
        org.mockito.Mockito.when(updateCustomerUseCase.updateCustomer(org.mockito.ArgumentMatchers.any())).thenReturn(updatedCustomer);
        
        CustomerUpdateRequest updateRequest = CustomerUpdateRequest.builder()
                .name(name)
                .email(email)
                .build();
        
        result = mockMvc.perform(put("/customer/{cpf}", cpf)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andReturn();
    }

    @Then("the customer should be updated successfully with status {int}")
    public void the_customer_should_be_updated_successfully_with_status(Integer status) {
        assertEquals(status.intValue(), result.getResponse().getStatus());
    }

    @Then("the response should contain the updated information")
    public void the_response_should_contain_the_updated_information() throws Exception {
        String responseContent = result.getResponse().getContentAsString();
        assertNotNull(responseContent);
        assertFalse(responseContent.isEmpty());
    }
}
