package com.lanchonete.production.core.application.ports;

import com.lanchonete.production.core.application.dto.CustomerDTO;

public interface CustomerServicePort {
    CustomerDTO getCustomerByCpf(String cpf);
}

