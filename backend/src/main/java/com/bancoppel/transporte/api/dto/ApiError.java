package com.bancoppel.transporte.api.dto;

import java.time.OffsetDateTime;

public record ApiError(String code, String message, OffsetDateTime timestamp) {
}
