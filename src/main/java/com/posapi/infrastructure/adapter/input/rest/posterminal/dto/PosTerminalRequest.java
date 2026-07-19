package com.posapi.infrastructure.adapter.input.rest.posterminal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PosTerminalRequest(
        @NotBlank(message = "El nombre de la terminal no puede estar vacío")
        @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
        String name,

        @Size(max = 255, message = "La ubicación no puede exceder los 255 caracteres")
        String location,

        @NotNull(message = "El estado activo es obligatorio")
        Boolean isActive
) {}
