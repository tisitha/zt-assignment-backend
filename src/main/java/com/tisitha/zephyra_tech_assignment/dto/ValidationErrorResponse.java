package com.tisitha.zephyra_tech_assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ValidationErrorResponse {
    private List<ErrorDTO> errors;

}