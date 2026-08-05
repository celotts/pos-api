package com.posapi.infrastructure.adapter.input.rest.cashaccount.mapper;

import com.posapi.domain.model.cashaccount.CashAccount;
import com.posapi.infrastructure.adapter.input.rest.cashaccount.dto.CashAccountRequest;
import com.posapi.infrastructure.adapter.input.rest.cashaccount.dto.CashAccountResponse;
import com.posapi.infrastructure.adapter.input.rest.mapper.AuditingMapperConfig;
import com.posapi.infrastructure.adapter.input.rest.mapper.IgnoreAuditingOnCreate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", config = AuditingMapperConfig.class)
public interface CashAccountRestMapper {

    @IgnoreAuditingOnCreate
    @Mapping(target = "currentBalance", source = "initialBalance")
    CashAccount toDomain(CashAccountRequest request);

    CashAccountResponse toResponse(CashAccount cashAccount);

    List<CashAccountResponse> toResponseList(List<CashAccount> cashAccounts);
}
