package com.posapi.infrastructure.adapter.input.rest.customer.mapper;

import com.posapi.domain.model.customer.Customer;
import com.posapi.infrastructure.adapter.input.rest.customer.dto.CustomerRequest;
import com.posapi.infrastructure.adapter.input.rest.customer.dto.CustomerResponse;
import com.posapi.infrastructure.adapter.input.rest.mapper.AuditingMapperConfig;
import com.posapi.infrastructure.adapter.input.rest.mapper.IgnoreAuditingOnCreate;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", config = AuditingMapperConfig.class)
public interface CustomerRestMapper {

    @IgnoreAuditingOnCreate
    Customer toDomain(CustomerRequest request);

    CustomerResponse toResponse(Customer customer);

    List<CustomerResponse> toResponseList(List<Customer> customers);
}
