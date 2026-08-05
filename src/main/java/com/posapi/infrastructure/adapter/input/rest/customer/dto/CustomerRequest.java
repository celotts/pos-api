package com.posapi.infrastructure.adapter.input.rest.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerRequest {
    @NotBlank
    private String fullName;

    @Email
    private String email;

    private String phoneNumber;
    private String address;
    private String rfc;
}
