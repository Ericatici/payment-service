package com.lanchonete.customer.core.application.usecases;

import com.lanchonete.customer.core.application.dto.CustomerDTO;
import com.lanchonete.customer.core.domain.model.Customer;

public interface CreateCustomerUseCase {
    Customer createCustomer(CustomerDTO customer); 
}


