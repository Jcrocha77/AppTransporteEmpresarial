package com.bancoppel.transporte.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReportReceipt(UUID reportId, String status, OffsetDateTime createdAt) {
}
