package com.bancoppel.transporte.integration;

import com.bancoppel.transporte.api.dto.AccidentReportRequest;
import java.util.UUID;

public interface AccidentNotificationClient {
    UUID notificarAccidente(AccidentReportRequest report);
}
