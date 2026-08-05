package com.posapi.infrastructure.adapter.input.rest.customer.dto;

import com.posapi.infrastructure.adapter.input.rest.dto.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse extends BaseResponse {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String rfc;
}
