package com.posapi.infrastructure.adapter.input.rest.purchase.mapper;

import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseRequest;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseResponse;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseItemRequest;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseItemResponse;
import com.posapi.shared.dto.PageResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PurchaseRestMapper {

    private final PurchaseItemRestMapper purchaseItemRestMapper;

    public PurchaseRestMapper(PurchaseItemRestMapper purchaseItemRestMapper) {
        this.purchaseItemRestMapper = purchaseItemRestMapper;
    }

    public PurchaseRequest toApplicationPurchaseRequest(PurchaseRequest restPurchaseRequest) {
        // Usar .items() para acceder a la lista de ítems del record PurchaseRequest
        if (restPurchaseRequest.items() != null) {
            List<PurchaseItemRequest> applicationItems = restPurchaseRequest.items().stream()
                    .map(purchaseItemRestMapper::toApplicationPurchaseItemRequest)
                    .collect(Collectors.toList());

        }
        return restPurchaseRequest; // Devolvemos el mismo record inmutable
    }

    public PurchaseResponse toRestPurchaseResponse(PurchaseResponse applicationPurchaseResponse) {
        // Usar .items() para acceder a la lista de ítems del record PurchaseResponse
        if (applicationPurchaseResponse.items() != null) {
            List<PurchaseItemResponse> restItems = applicationPurchaseResponse.items().stream()
                    .map(purchaseItemRestMapper::toRestPurchaseItemResponse)
                    .toList();

        }
        return applicationPurchaseResponse; // Devolvemos el mismo record inmutable
    }

    public List<PurchaseResponse> toRestPurchaseResponseList(List<PurchaseResponse> applicationPurchaseResponseList) {
        return applicationPurchaseResponseList.stream()
                .map(this::toRestPurchaseResponse)
                .collect(Collectors.toList());
    }

    public PageResponse<PurchaseResponse> toRestPageResponse(PageResponse<PurchaseResponse> applicationPageResponse) {
        // Usar los métodos de acceso correctos para los componentes del record PageResponse
        List<PurchaseResponse> restContent = applicationPageResponse.content().stream()
                .map(this::toRestPurchaseResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(restContent,
                applicationPageResponse.pageNumber(),
                applicationPageResponse.pageSize(),
                applicationPageResponse.totalElements(),
                applicationPageResponse.totalPages(),
                applicationPageResponse.isLast());
    }
}
