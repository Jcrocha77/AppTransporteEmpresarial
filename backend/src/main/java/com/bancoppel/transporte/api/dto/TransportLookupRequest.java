package com.bancoppel.transporte.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TransportLookupRequest(
        @NotBlank @Size(max = 80) String employeeNumber,
        @NotBlank @Size(max = 160) String company
) {
}
