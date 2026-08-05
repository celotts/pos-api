package com.posapi.infrastructure.adapter.input.rest.cashaccount.dto;

import com.posapi.domain.model.cashaccount.CashAccountType;
import com.posapi.infrastructure.adapter.input.rest.dto.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CashAccountResponse extends BaseResponse {
    private String name;
    private CashAccountType accountType;
    private BigDecimal currentBalance;
    private String currency;
}
