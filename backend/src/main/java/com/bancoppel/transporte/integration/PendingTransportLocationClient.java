package com.bancoppel.transporte.integration;

import org.springframework.stereotype.Component;

@Component
public class PendingTransportLocationClient implements TransportLocationClient {
    @Override
    public TransportLocationResponse getCurrentLocation(String employeeNumber, String company) {
        throw new IntegrationPendingException("consulta de ubicación del transporte");
    }
}
