package com.posapi.application.port.product;

import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductRequest;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductManagementPort {

    /**
     * Crea un nuevo producto.
     * @param request DTO con los datos del producto a crear.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un DTO de respuesta con el producto creado.
     */
    ProductResponse createProduct(ProductRequest request, UUID currentUserId);

    /**
     * Obtiene un producto por su ID.
     * @param id ID único del producto.
     * @return Un Optional que contiene el DTO de respuesta si el producto existe.
     */
    Optional<ProductResponse> getProductById(UUID id);

    /**
     * Obtiene una lista de todos los productos.
     * @return Una lista de DTOs de respuesta de productos.
     */
    List<ProductResponse> getAllProducts();

    /**
     * Actualiza un producto existente.
     * @param id ID del producto a actualizar.
     * @param request DTO con los datos actualizados del producto.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un Optional que contiene el DTO de respuesta con el producto actualizado.
     */
    Optional<ProductResponse> updateProduct(UUID id, ProductRequest request, UUID currentUserId);

    /**
     * Elimina lógicamente un producto.
     * @param id ID del producto a eliminar.
     * @param currentUserId ID del usuario que realiza la operación.
     */
    void deleteProduct(UUID id, UUID currentUserId);

    /**
     * Obtiene un producto por su SKU.
     * @param sku SKU del producto.
     * @return Un Optional que contiene el DTO de respuesta si el producto existe.
     */
    Optional<ProductResponse> getProductBySku(String sku);
}
