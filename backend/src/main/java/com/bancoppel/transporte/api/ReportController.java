package com.bancoppel.transporte.api;

import com.bancoppel.transporte.api.dto.AccidentReportRequest;
import com.bancoppel.transporte.api.dto.DelayReportRequest;
import com.bancoppel.transporte.api.dto.ReportReceipt;
import com.bancoppel.transporte.integration.AccidentNotificationClient;
import com.bancoppel.transporte.report.ReportService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Validated
@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;
    private final AccidentNotificationClient accidentNotificationClient;

    public ReportController(ReportService reportService, AccidentNotificationClient accidentNotificationClient) {
        this.reportService = reportService;
        this.accidentNotificationClient = accidentNotificationClient;
    }

    @PostMapping("/delay")
    public ReportReceipt reportDelay(@Valid @RequestBody DelayReportRequest request) {
        return reportService.createDelayReport(request);
    }

    @PostMapping("/accident")
    public ReportReceipt reportAccident(@Valid @RequestBody AccidentReportRequest request) {
        UUID notificationId = accidentNotificationClient.notificarAccidente(request);
        return new ReportReceipt(notificationId, "NOTIFICATION_SENT", java.time.OffsetDateTime.now());
    }

    @PostMapping(value = "/dirty-unit", consumes = "multipart/form-data")
    public ReportReceipt reportDirtyUnit(
            @RequestPart("employeeNumber") @NotBlank String employeeNumber,
            @RequestPart("company") @NotBlank String company,
            @RequestPart("description") @NotBlank String description,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        if (employeeNumber.length() > 80 || company.length() > 160) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los datos de empleado o empresa exceden la longitud permitida.");
        }
        return reportService.createDirtyUnitReport(employeeNumber, company, description, images);
    }
}
