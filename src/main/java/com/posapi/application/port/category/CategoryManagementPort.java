package com.posapi.application.port.category;

import com.posapi.infrastructure.adapter.input.rest.category.dto.CategoryRequest; // Asumo que tendrás un DTO de Request
import com.posapi.infrastructure.adapter.input.rest.category.dto.CategoryResponse; // Asumo que tendrás un DTO de Response
import com.posapi.shared.dto.PageResponse; // Para paginación
import org.springframework.data.domain.Pageable; // Para paginación

import java.util.Optional;
import java.util.UUID;

public interface CategoryManagementPort { // CORREGIDO: Nombre de la interfaz

    /**
     * Crea una nueva categoría.
     * @param request DTO con los datos de la categoría a crear.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un DTO de respuesta con la categoría creada.
     */
    CategoryResponse createCategory(CategoryRequest request, UUID currentUserId);

    /**
     * Obtiene una categoría por su ID.
     * @param id ID único de la categoría.
     * @return Un Optional que contiene el DTO de respuesta si la categoría existe.
     */
    Optional<CategoryResponse> getCategoryById(UUID id);

    /**
     * Obtiene una lista paginada de todas las categorías.
     * @param pageable Objeto Pageable con los parámetros de paginación.
     * @return Una respuesta paginada de DTOs de categorías.
     */
    PageResponse<CategoryResponse> getAllCategories(Pageable pageable);

    /**
     * Actualiza una categoría existente.
     * @param id ID de la categoría a actualizar.
     * @param request DTO con los datos actualizados.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un Optional que contiene el DTO de respuesta de la categoría actualizada si existe.
     */
    Optional<CategoryResponse> updateCategory(UUID id, CategoryRequest request, UUID currentUserId);

    /**
     * Elimina lógicamente una categoría.
     * @param id ID de la categoría a eliminar.
     * @param currentUserId ID del usuario que realiza la operación.
     */
    void deleteCategory(UUID id, UUID currentUserId);

    /**
     * Obtiene una categoría por su nombre.
     * @param name Nombre de la categoría.
     * @return Un Optional que contiene el DTO de respuesta si la categoría existe.
     */
    Optional<CategoryResponse> getCategoryByName(String name);
}
