package com.posapi.application.port.sale;

import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleRequest;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleResponse;
import com.posapi.infrastructure.adapter.input.rest.saleItem.dto.SaleItemRequest;

import java.util.List;
import java.util.UUID;

public interface SaleMagnamentPort {

    /**
     * Crea una nueva venta.
     * @param request DTO con los datos de la venta.
     * @return SaleResponse DTO con los datos de la venta creada.
     */
    SaleResponse createSale(SaleRequest request);

    /**
     * Obtiene una venta por su ID.
     * @param saleId ID de la venta.
     * @return SaleResponse DTO con los datos de la venta.
     */
    SaleResponse getSaleById(UUID saleId);

    /**
     * Actualiza una venta existente.
     * @param saleId ID de la venta a actualizar.
     * @param request DTO con los datos actualizados de la venta.
     * @return SaleResponse DTO con los datos de la venta actualizada.
     */
    SaleResponse updateSale(UUID saleId, SaleRequest request);

    /**
     * Elimina lógicamente una venta por su ID.
     * @param saleId ID de la venta a eliminar.
     */
    void deleteSale(UUID saleId);

    /**
     * Agrega un ítem a una venta existente.
     * @param saleId ID de la venta.
     * @param itemRequest DTO con los datos del ítem a agregar.
     * @return SaleResponse DTO con los datos de la venta actualizada.
     */
    SaleResponse addSaleItem(UUID saleId, SaleItemRequest itemRequest);

    /**
     * Actualiza un ítem específico de una venta.
     * @param saleId ID de la venta.
     * @param itemId ID del ítem de venta a actualizar.
     * @param itemRequest DTO con los datos actualizados del ítem.
     * @return SaleResponse DTO con los datos de la venta actualizada.
     */
    SaleResponse updateSaleItem(UUID saleId, UUID itemId, SaleItemRequest itemRequest);

    /**
     * Elimina un ítem específico de una venta.
     * @param saleId ID de la venta.
     * @param itemId ID del ítem de venta a eliminar.
     * @return SaleResponse DTO con los datos de la venta actualizada.
     */
    SaleResponse deleteSaleItem(UUID saleId, UUID itemId);

    /**
     * Lista todas las ventas.
     * @return Lista de SaleResponse DTOs.
     */
    List<SaleResponse> getAllSales();
}
