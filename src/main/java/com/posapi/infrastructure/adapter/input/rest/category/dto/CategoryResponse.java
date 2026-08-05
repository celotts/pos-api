package com.posapi.infrastructure.adapter.input.rest.category.dto;

import com.posapi.infrastructure.adapter.input.rest.dto.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse extends BaseResponse {
    private String name;
}
