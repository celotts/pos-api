package com.posapi.infrastructure.adapter.input.rest.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GenerateDescriptionRequest(
        @NotBlank(message = "Product name cannot be blank")
        String productName,
        @NotBlank(message = "Characteristics cannot be blank")
        @Size(max = 500, message = "Characteristics must not exceed 500 characters")
        String characteristics
) {}
