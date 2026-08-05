package com.posapi.infrastructure.adapter.input.rest.purchase.mapper;

import com.posapi.domain.model.purchase.Purchase;
import com.posapi.infrastructure.adapter.input.rest.mapper.AuditingMapperConfig;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
    componentModel = "spring",
    config = AuditingMapperConfig.class,
    uses = PurchaseItemRestMapper.class
)
public interface PurchaseRestMapper {

    PurchaseResponse toResponse(Purchase purchase);

    List<PurchaseResponse> toResponseList(List<Purchase> purchases);
}
