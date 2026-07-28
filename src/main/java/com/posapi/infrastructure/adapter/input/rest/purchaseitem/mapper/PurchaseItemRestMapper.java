package com.posapi.infrastructure.adapter.input.rest.purchaseitem.mapper;

import com.posapi.infrastructure.adapter.input.rest.purchaseitem.dto.PurchaseItemRequest;
import com.posapi.infrastructure.adapter.input.rest.purchaseitem.dto.PurchaseItemResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PurchaseItemRestMapper {

    public PurchaseItemRequest toApplicationPurchaseItemRequest(PurchaseItemRequest restPurchaseItemRequest) {
        return restPurchaseItemRequest;
    }

    public PurchaseItemResponse toRestPurchaseItemResponse(PurchaseItemResponse applicationPurchaseItemResponse) {
        return applicationPurchaseItemResponse;
    }

    public List<PurchaseItemRequest> toApplicationPurchaseItemRequestList(List<PurchaseItemRequest> restPurchaseItemRequests) {
        return restPurchaseItemRequests.stream()
                .map(this::toApplicationPurchaseItemRequest)
                .collect(Collectors.toList());
    }

    public List<PurchaseItemResponse> toRestPurchaseItemResponseList(List<PurchaseItemResponse> applicationPurchaseItemResponses) {
        return applicationPurchaseItemResponses.stream()
                .map(this::toRestPurchaseItemResponse)
                .collect(Collectors.toList());
    }
}
