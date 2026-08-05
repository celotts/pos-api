package com.posapi.application.port.sale;

import com.posapi.domain.model.sale.PaymentStatus;
import com.posapi.domain.model.sale.SaleStatus;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleRequest;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleResponse;
import com.posapi.shared.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface SaleManagementPort {

    SaleResponse createSale(SaleRequest request, UUID currentUserId);

    Optional<SaleResponse> getSaleById(UUID id);

    PageResponse<SaleResponse> getAllSales(Pageable pageable);

    Optional<SaleResponse> updateSale(UUID id, SaleRequest request, UUID currentUserId);

    void deleteSale(UUID id, UUID currentUserId);

    Optional<SaleResponse> updateSaleStatus(UUID id, SaleStatus newStatus, UUID currentUserId);

    Optional<SaleResponse> updatePaymentStatus(UUID id, PaymentStatus newPaymentStatus, UUID currentUserId);
}
