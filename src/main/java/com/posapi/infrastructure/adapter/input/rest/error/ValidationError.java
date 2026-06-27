package com.posapi.infrastructure.adapter.input.rest.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ValidationError {
    private String field;
    private String message;
}
