package com.posapi.infrastructure.adapter.input.rest.sale.mapper;

import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleItemRequest;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleItemResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SaleItemRestMapper {

    public SaleItemRequest toApplicationSaleItemRequest(SaleItemRequest restSaleItemRequest) {
        return restSaleItemRequest;
    }

    public SaleItemResponse toRestSaleItemResponse(SaleItemResponse applicationSaleItemResponse) {
        return applicationSaleItemResponse;
    }

    public List<SaleItemRequest> toApplicationSaleItemRequestList(List<SaleItemRequest> restSaleItemRequests) {
        return restSaleItemRequests.stream()
                .map(this::toApplicationSaleItemRequest)
                .collect(Collectors.toList());
    }

    public List<SaleItemResponse> toRestSaleItemResponseList(List<SaleItemResponse> applicationSaleItemResponses) {
        return applicationSaleItemResponses.stream()
                .map(this::toRestSaleItemResponse)
                .collect(Collectors.toList());
    }
}
