package com.posapi.infrastructure.adapter.input.rest.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
    @NotBlank(message = "Category name cannot be blank")
    @Size(min = 2, max = 255, message = "Category name must be between 2 and 255 characters")
    String name
) {}
