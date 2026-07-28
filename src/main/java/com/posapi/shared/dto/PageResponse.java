package com.posapi.shared.dto;

import java.util.List;

public record PageResponse<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean isLast
) {
    // Los métodos de acceso (getters) para los componentes de un record se generan automáticamente.
    // No es necesario definirlos explícitamente a menos que se quiera añadir lógica personalizada.
    // Los métodos incorrectos que causaban "missing return statement" han sido eliminados.
}
