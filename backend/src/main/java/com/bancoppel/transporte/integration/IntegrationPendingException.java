package com.bancoppel.transporte.integration;

public class IntegrationPendingException extends RuntimeException {
    public IntegrationPendingException(String integrationName) {
        super("La integración " + integrationName + " aún no está configurada.");
    }
}
