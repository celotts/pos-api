package com.posapi.infrastructure.adapter.input.rest.saleItem.mapper;

import com.posapi.infrastructure.adapter.input.rest.saleItem.dto.SaleItemRequest;
import com.posapi.infrastructure.adapter.input.rest.saleItem.dto.SaleItemResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SaleItemRestMapper {

    /**
     * Convierte un SaleItemRequest de la capa REST a un SaleItemRequest de la capa de aplicación.
     * Actualmente, son el mismo DTO, por lo que es una conversión de identidad.
     * @param restSaleItemRequest El SaleItemRequest de la capa REST.
     * @return El SaleItemRequest para la capa de aplicación.
     */
    public SaleItemRequest toApplicationSaleItemRequest(SaleItemRequest restSaleItemRequest) {
        return restSaleItemRequest;
    }

    /**
     * Convierte un SaleItemResponse de la capa de aplicación a un SaleItemResponse de la capa REST.
     * Actualmente, son el mismo DTO, por lo que es una conversión de identidad.
     * @param applicationSaleItemResponse El SaleItemResponse de la capa de aplicación.
     * @return El SaleItemResponse para la capa REST.
     */
    public SaleItemResponse toRestSaleItemResponse(SaleItemResponse applicationSaleItemResponse) {
        return applicationSaleItemResponse;
    }

    /**
     * Convierte una lista de SaleItemRequest de la capa REST a una lista para la capa de aplicación.
     * @param restSaleItemRequests La lista de SaleItemRequest de la capa REST.
     * @return La lista de SaleItemRequest para la capa de aplicación.
     */
    public List<SaleItemRequest> toApplicationSaleItemRequestList(List<SaleItemRequest> restSaleItemRequests) {
        return restSaleItemRequests.stream()
                .map(this::toApplicationSaleItemRequest)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una lista de SaleItemResponse de la capa de aplicación a una lista para la capa REST.
     * @param applicationSaleItemResponses La lista de SaleItemResponse de la capa de aplicación.
     * @return La lista de SaleItemResponse para la capa REST.
     */
    public List<SaleItemResponse> toRestSaleItemResponseList(List<SaleItemResponse> applicationSaleItemResponses) {
        return applicationSaleItemResponses.stream()
                .map(this::toRestSaleItemResponse)
                .collect(Collectors.toList());
    }
}
