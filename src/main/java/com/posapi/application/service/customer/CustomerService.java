package com.posapi.application.service.customer;

import com.posapi.application.port.customer.CustomerManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.customer.Customer;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.CustomerRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.customer.dto.CustomerRequest;
import com.posapi.infrastructure.adapter.input.rest.customer.dto.CustomerResponse;
import com.posapi.infrastructure.adapter.input.rest.customer.mapper.CustomerRestMapper;
import com.posapi.infrastructure.security.SecurityContextHelper;
import com.posapi.shared.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService implements CustomerManagementPort {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper;
    private final CustomerRestMapper customerRestMapper;

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request, UUID currentUserId) {
        if (request.getRfc() != null && customerRepository.existsByRfc(request.getRfc())) {
            throw new DuplicateResourceException("Customer with RFC '" + request.getRfc() + "' already exists.");
        }
        if (request.getEmail() != null && customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Customer with email '" + request.getEmail() + "' already exists.");
        }

        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        Customer newCustomer = Customer.createNew(
                request.getFullName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getAddress(),
                request.getRfc(),
                currentUserId,
                currentUserRoleId
        );

        Customer savedCustomer = customerRepository.save(newCustomer);
        return customerRestMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerResponse> getCustomerById(UUID id) {
        return customerRepository.findById(id).map(customerRestMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CustomerResponse> getAllCustomers(Pageable pageable) {
        Page<Customer> customersPage = customerRepository.findAll(pageable);
        List<CustomerResponse> content = customersPage.getContent().stream()
                .map(customerRestMapper::toResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                content,
                customersPage.getNumber(),
                customersPage.getSize(),
                customersPage.getTotalElements(),
                customersPage.getTotalPages(),
                customersPage.isLast()
        );
    }

    @Override
    @Transactional
    public Optional<CustomerResponse> updateCustomer(UUID id, CustomerRequest request, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        return customerRepository.findById(id).map(existingCustomer -> {
            if (request.getRfc() != null && !request.getRfc().equals(existingCustomer.getRfc())) {
                if (customerRepository.existsByRfc(request.getRfc())) {
                    throw new DuplicateResourceException("Customer with RFC '" + request.getRfc() + "' already exists.");
                }
            }
            if (request.getEmail() != null && !request.getEmail().equals(existingCustomer.getEmail())) {
                if (customerRepository.existsByEmail(request.getEmail())) {
                    throw new DuplicateResourceException("Customer with email '" + request.getEmail() + "' already exists.");
                }
            }
            existingCustomer.updateDetails(
                    request.getFullName(),
                    request.getEmail(),
                    request.getPhoneNumber(),
                    request.getAddress(),
                    request.getRfc(),
                    currentUserId,
                    currentUserRoleId
            );
            Customer updatedCustomer = customerRepository.save(existingCustomer);
            return customerRestMapper.toResponse(updatedCustomer);
        });
    }

    @Override
    @Transactional
    public void deleteCustomer(UUID id, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        customerRepository.findById(id).ifPresent(existingCustomer -> {
            existingCustomer.markAsDeleted(currentUserId, currentUserRoleId);
            customerRepository.save(existingCustomer);
            log.info("Customer with id {} marked as deleted by user {}", id, currentUserId);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerResponse> getCustomerByRfc(String rfc) {
        return customerRepository.findByRfc(rfc).map(customerRestMapper::toResponse);
    }
}
