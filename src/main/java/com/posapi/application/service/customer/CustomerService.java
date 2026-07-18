package com.posapi.application.service.customer;

import com.posapi.application.port.customer.CustomerInputPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.exception.ResourceNotFoundException;
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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService implements CustomerInputPort {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper;
    private final CustomerRestMapper customerRestMapper;

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request, UUID currentUserId) {
        if (customerRepository.existsByRfc(request.rfc())) {
            throw new DuplicateResourceException("Customer with RFC '" + request.rfc() + "' already exists.");
        }
        if (request.email() != null && customerRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Customer with email '" + request.email() + "' already exists.");
        }

        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        Customer newCustomer = Customer.createNew(
                request.fullName(),
                request.email(),
                request.phoneNumber(),
                request.address(),
                request.rfc(),
                currentUserId,
                currentUserRoleId
        );

        Customer savedCustomer = customerRepository.save(newCustomer);
        return mapToCustomerResponse(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerResponse> getCustomerById(UUID id) {
        return customerRepository.findById(id).map(this::mapToCustomerResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CustomerResponse> getAllCustomers(Pageable pageable) {
        Page<Customer> customersPage = customerRepository.findAll(pageable);
        List<CustomerResponse> content = customersPage.getContent().stream()
                .map(this::mapToCustomerResponse)
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
            if (request.rfc() != null && !request.rfc().equals(existingCustomer.getRfc())) {
                if (customerRepository.existsByRfc(request.rfc())) {
                    throw new DuplicateResourceException("Customer with RFC '" + request.rfc() + "' already exists.");
                }
            }
            if (request.email() != null && !request.email().equals(existingCustomer.getEmail())) {
                if (customerRepository.existsByEmail(request.email())) {
                    throw new DuplicateResourceException("Customer with email '" + request.email() + "' already exists.");
                }
            }
            existingCustomer.updateDetails(
                    request.fullName(),
                    request.email(),
                    request.phoneNumber(),
                    request.address(),
                    request.rfc(),
                    currentUserId,
                    currentUserRoleId
            );
            Customer updatedCustomer = customerRepository.save(existingCustomer);
            return mapToCustomerResponse(updatedCustomer);
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
        return customerRepository.findByRfc(rfc).map(this::mapToCustomerResponse);
    }

    private CustomerResponse mapToCustomerResponse(Customer customer) {
        Set<UUID> userIds = Stream.of(
                customer.getCreatedByUserId(),
                customer.getUpdatedByUserId(),
                customer.getDeletedByUserId()
        ).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<UUID, String> userNames = fetchUserNames(userIds);

        String createdByName = userNames.getOrDefault(customer.getCreatedByUserId(), null);
        String updatedByName = userNames.getOrDefault(customer.getUpdatedByUserId(), null);
        String deletedByName = userNames.getOrDefault(customer.getDeletedByUserId(), null);

        return CustomerResponse.fromDomain(customer, createdByName, updatedByName, deletedByName);
    }

    private Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }
}
