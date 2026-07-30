package com.posapi.infrastructure.adapter.input.rest.sale.mapper;

import com.posapi.infrastructure.adapter.input.rest.saleItem.dto.SaleItemRequest;
import com.posapi.infrastructure.adapter.input.rest.saleItem.dto.SaleItemResponse;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleRequest;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleResponse;
import com.posapi.infrastructure.adapter.input.rest.saleItem.mapper.SaleItemRestMapper; // Importar el SaleItemRestMapper
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SaleRestMapper {

    private final SaleItemRestMapper saleItemRestMapper; // Inyectar SaleItemRestMapper

    public SaleRestMapper(SaleItemRestMapper saleItemRestMapper) {
        this.saleItemRestMapper = saleItemRestMapper;
    }

    public SaleRequest toApplicationSaleRequest(SaleRequest restSaleRequest) {
        // Si SaleRequest contiene una lista de SaleItemRequest, mapearla aquí
        if (restSaleRequest.getItems() != null) {
            List<SaleItemRequest> applicationItems = saleItemRestMapper.toApplicationSaleItemRequestList(restSaleRequest.getItems());
            restSaleRequest.setItems(applicationItems);
        }
        return restSaleRequest; // O mapear a un objeto de dominio Sale
    }

    public SaleResponse toRestSaleResponse(SaleResponse applicationSaleResponse) {
        // Si SaleResponse contiene una lista de SaleItemResponse, mapearla aquí
        if (applicationSaleResponse.getItems() != null) {
            List<SaleItemResponse> restItems = saleItemRestMapper.toRestSaleItemResponseList(applicationSaleResponse.getItems());
            applicationSaleResponse.setItems(restItems);
        }
        return applicationSaleResponse; // O mapear desde un objeto de dominio Sale
    }

    public List<SaleResponse> toRestSaleResponseList(List<SaleResponse> applicationSaleResponseList) {
        return applicationSaleResponseList.stream()
                .map(this::toRestSaleResponse)
                .collect(Collectors.toList());
    }
}
