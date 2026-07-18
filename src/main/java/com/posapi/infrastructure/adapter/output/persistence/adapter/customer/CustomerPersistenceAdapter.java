package com.posapi.infrastructure.adapter.output.persistence.adapter.customer;

import com.posapi.domain.model.customer.Customer;
import com.posapi.domain.port.output.CustomerRepository;
import com.posapi.infrastructure.adapter.output.persistence.mapper.customer.CustomerPersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.customer.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomerPersistenceAdapter implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerPersistenceMapper customerPersistenceMapper;

    @Override
    public Customer save(Customer customer) {
        return customerPersistenceMapper.toDomain(
                customerJpaRepository.save(customerPersistenceMapper.toEntity(customer))
        );
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return customerJpaRepository.findById(id)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Page<Customer> findAll(Pageable pageable) {
        return customerJpaRepository.findAll(pageable)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return customerJpaRepository.findAll().stream()
                .map(customerPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Customer> findByRfc(String rfc) {
        return customerJpaRepository.findByRfc(rfc)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByRfc(String rfc) {
        return customerJpaRepository.existsByRfc(rfc);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return customerJpaRepository.findByEmail(email)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return customerJpaRepository.existsByEmail(email);
    }
}
