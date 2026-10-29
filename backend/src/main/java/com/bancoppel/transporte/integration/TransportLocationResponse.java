package com.bancoppel.transporte.integration;

import java.time.OffsetDateTime;

public record TransportLocationResponse(
        double latitude,
        double longitude,
        String vehicleLabel,
        String routeName,
        Integer etaMinutes,
        OffsetDateTime updatedAt
) {
}
