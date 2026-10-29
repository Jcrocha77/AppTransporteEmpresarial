package com.bancoppel.transporte.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccidentReportRequest(
        @NotBlank String employeeNumber,
        @NotBlank String company,
        @Size(max = 500) String details
) {
}
