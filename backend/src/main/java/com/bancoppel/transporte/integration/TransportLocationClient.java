package com.bancoppel.transporte.integration;

public interface TransportLocationClient {
    TransportLocationResponse getCurrentLocation(String employeeNumber, String company);
}
