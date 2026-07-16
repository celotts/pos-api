package com.posapi.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para respuestas paginadas.
 *
 * @param <T> el tipo de elementos en la página
 */
@Data
@NoArgsConstructor
@AllArgsConstructor // Este constructor de Lombok es suficiente
public class PageResponse<T> {

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;

    // El constructor específico para CategoryResponse y el constructor vacío se eliminan
    // ya que @AllArgsConstructor y @NoArgsConstructor de Lombok los manejan.

    // Puedes añadir constructores personalizados si necesitas lógica específica,
    // pero para un DTO simple, los de Lombok son suficientes.
    public PageResponse(List<T> content, int pageNumber, int pageSize, long totalElements, int totalPages, boolean isLast) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.isFirst = pageNumber == 0; // Asumiendo que la primera página es 0
        this.isLast = isLast;
        this.hasNext = pageNumber < totalPages - 1;
        this.hasPrevious = pageNumber > 0;
    }
}
