package com.lanchonete.customer.adapter.driven.persistence.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lanchonete.customer.adapter.driven.persistence.entities.CustomerEntity;

@Repository
public interface JpaCustomerRepository extends JpaRepository<CustomerEntity, String> {
}


