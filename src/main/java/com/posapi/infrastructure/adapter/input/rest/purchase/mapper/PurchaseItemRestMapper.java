package com.posapi.infrastructure.adapter.input.rest.purchase.mapper;

import com.posapi.domain.model.purchase.PurchaseItem;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PurchaseItemRestMapper {

    PurchaseItemResponse toResponse(PurchaseItem purchaseItem);

    List<PurchaseItemResponse> toResponseList(List<PurchaseItem> purchaseItems);
}
