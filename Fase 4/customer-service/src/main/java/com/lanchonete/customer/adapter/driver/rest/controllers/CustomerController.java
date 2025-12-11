package com.lanchonete.customer.adapter.driver.rest.controllers;

import static com.lanchonete.customer.adapter.infrastructure.config.ContextLogger.checkTraceId;
import static com.lanchonete.customer.adapter.infrastructure.config.ContextLogger.REQUEST_TRACE_ID;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lanchonete.customer.adapter.driver.rest.mappers.CustomerMapper;
import com.lanchonete.customer.adapter.driver.rest.requests.CustomerRequest;
import com.lanchonete.customer.adapter.driver.rest.requests.CustomerUpdateRequest;
import com.lanchonete.customer.adapter.driver.rest.responses.CustomerResponse;
import com.lanchonete.customer.core.application.usecases.CreateCustomerUseCase;
import com.lanchonete.customer.core.application.usecases.FindCustomerUseCase;
import com.lanchonete.customer.core.application.usecases.UpdateCustomerUseCase;
import com.lanchonete.customer.core.domain.model.Customer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Customer", description = "Operations related to customers")
@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/customer")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final FindCustomerUseCase findCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;

    @Operation(summary = "Create a new customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Customer created",
                     content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping(path = "/save")
    public ResponseEntity<CustomerResponse> createCustomer(
        @RequestHeader(value = REQUEST_TRACE_ID, required = false) String requestTraceId,
        @Valid @RequestBody final CustomerRequest customer) {
            checkTraceId(requestTraceId);

            log.info("Received request to create customer with CPF: {}", customer.getCpf());

            Customer createdCustomer = createCustomerUseCase.createCustomer(customer.toDto());
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomerMapper.toCustomerResponse(createdCustomer));
    }


    @Operation(summary = "Update customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Customer updated",
                     content = @Content(schema = @Schema(implementation = CustomerUpdateRequest.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping(path = "/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
        @RequestHeader(value = REQUEST_TRACE_ID, required = false) String requestTraceId,
        @PathVariable String id, 
        @Valid @RequestBody final CustomerUpdateRequest customer) {
            checkTraceId(requestTraceId);

            log.info("Received request to update customer with CPF: {}", id);

            Customer updatedCustomer = updateCustomerUseCase.updateCustomer(customer.toDto(id));
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(CustomerMapper.toCustomerResponse(updatedCustomer));
    }


    @Operation(summary = "Get a customer by CPF")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer found",
                     content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/{cpf}")
    public ResponseEntity<CustomerResponse> getCustomerByCpf(
        @RequestHeader(value = REQUEST_TRACE_ID, required = false) String requestTraceId, 
        @PathVariable final String cpf) {
            checkTraceId(requestTraceId);

            log.info("Received request to get customer with CPF: {}", cpf);

            Optional<Customer> customer = findCustomerUseCase.getCustomerOptionalByCpf(cpf);
            Optional<CustomerResponse> customerResponse = customer.map(CustomerMapper::toCustomerResponse);

            return customerResponse.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}


