package com.posapi.application.port.sale;

import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleRequest;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleResponse;
import com.posapi.infrastructure.adapter.input.rest.saleItem.dto.SaleItemRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface SaleMagnamentPort {

    SaleResponse createSale(SaleRequest request);

    SaleResponse getSaleById(UUID saleId);

    SaleResponse updateSale(UUID saleId, SaleRequest request, UUID currentUserId);

    void deleteSale(UUID saleId);

    SaleResponse addSaleItem(UUID saleId, SaleItemRequest itemRequest);

    SaleResponse updateSaleItem(UUID saleId, UUID itemId, SaleItemRequest itemRequest, UUID currentUserId);

    SaleResponse deleteSaleItem(UUID saleId, UUID itemId);

    List<SaleResponse> getAllSales();

    Page<SaleResponse> getAllSales(Pageable pageable);
}
