package com.bancoppel.transporte.integration;

import com.bancoppel.transporte.api.dto.AccidentReportRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PendingAccidentNotificationClient implements AccidentNotificationClient {
    @Override
    public UUID notificarAccidente(AccidentReportRequest report) {
        throw new IntegrationPendingException("notificarAccidente");
    }
}
