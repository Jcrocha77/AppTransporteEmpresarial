package com.bancoppel.transporte.api;

import com.bancoppel.transporte.integration.TransportLocationClient;
import com.bancoppel.transporte.integration.TransportLocationResponse;
import com.bancoppel.transporte.api.dto.TransportLookupRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/transport")
public class TransportController {
    private final TransportLocationClient locationClient;

    public TransportController(TransportLocationClient locationClient) {
        this.locationClient = locationClient;
    }

    @PostMapping("/location")
    public TransportLocationResponse getLocation(@Valid @RequestBody TransportLookupRequest request) {
        return locationClient.getCurrentLocation(request.employeeNumber().trim(), request.company().trim());
    }
}
