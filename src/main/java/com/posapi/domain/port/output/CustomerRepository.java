package com.posapi.domain.port.output;

import com.posapi.domain.model.customer.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(UUID id);
    Page<Customer> findAll(Pageable pageable);
    List<Customer> findAll();
    Optional<Customer> findByRfc(String rfc);
    boolean existsByRfc(String rfc);
    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsById(UUID id);
}
