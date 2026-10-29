package com.bancoppel.transporte.api.dto;

import com.bancoppel.transporte.report.DelayReason;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DelayReportRequest(
        @NotBlank String employeeNumber,
        @NotBlank String company,
        @NotNull DelayReason reason
) {
}
