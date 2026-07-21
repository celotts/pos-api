package com.posapi.application.port.purchase;

import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseRequest;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseResponse;
import com.posapi.shared.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface PurchaseManagementPort {

    PurchaseResponse createPurchase(PurchaseRequest request, UUID currentUserId);

    Optional<PurchaseResponse> getPurchaseById(UUID id);

    PageResponse<PurchaseResponse> getAllPurchases(Pageable pageable);

    Optional<PurchaseResponse> updatePurchase(UUID id, PurchaseRequest request, UUID currentUserId);

    void deletePurchase(UUID id, UUID currentUserId);
}
