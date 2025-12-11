package com.lanchonete.customer.core.application.usecases;

import com.lanchonete.customer.core.application.dto.CustomerDTO;
import com.lanchonete.customer.core.domain.model.Customer;

public interface UpdateCustomerUseCase {
    Customer updateCustomer(CustomerDTO customer);
}


