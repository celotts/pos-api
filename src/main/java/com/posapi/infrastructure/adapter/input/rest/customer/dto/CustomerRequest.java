package com.posapi.infrastructure.adapter.input.rest.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CustomerRequest (
    @NotBlank String fullName,
    @NotBlank @Email String email,
    @NotBlank String phoneNumber,
    @NotBlank String address,
    @NotBlank String rfc
){}
