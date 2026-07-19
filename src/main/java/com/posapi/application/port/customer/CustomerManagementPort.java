package com.posapi.application.port.customer;

import com.posapi.infrastructure.adapter.input.rest.customer.dto.CustomerRequest;
import com.posapi.infrastructure.adapter.input.rest.customer.dto.CustomerResponse;
import com.posapi.shared.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CustomerManagementPort {
    CustomerResponse createCustomer(CustomerRequest request, UUID currentUserId);
    Optional<CustomerResponse> getCustomerById(UUID id);
    PageResponse<CustomerResponse> getAllCustomers(Pageable pageable);
    Optional<CustomerResponse> updateCustomer(UUID id, CustomerRequest request, UUID currentUserId);
    void deleteCustomer(UUID id, UUID currentUserId);
    Optional<CustomerResponse> getCustomerByRfc(String rfc);
}
