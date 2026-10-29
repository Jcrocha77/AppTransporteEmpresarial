package com.bancoppel.transporte.report;

import com.bancoppel.transporte.api.dto.DelayReportRequest;
import com.bancoppel.transporte.api.dto.ReportReceipt;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {
    private final DelayReportRepository delayReports;
    private final DirtyUnitReportRepository dirtyUnitReports;
    private final UploadStorageService uploadStorage;

    public ReportService(DelayReportRepository delayReports, DirtyUnitReportRepository dirtyUnitReports,
                         UploadStorageService uploadStorage) {
        this.delayReports = delayReports;
        this.dirtyUnitReports = dirtyUnitReports;
        this.uploadStorage = uploadStorage;
    }

    @Transactional
    public ReportReceipt createDelayReport(DelayReportRequest request) {
        DelayReport report = delayReports.save(new DelayReport(
                request.employeeNumber().trim(), request.company().trim(), request.reason()));
        return new ReportReceipt(report.getId(), "REGISTERED", report.getCreatedAt());
    }

    @Transactional
    public ReportReceipt createDirtyUnitReport(String employeeNumber, String company,
                                                String description, List<org.springframework.web.multipart.MultipartFile> images) {
        String normalizedDescription = description == null ? "" : description.trim();
        if (normalizedDescription.isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "La descripción es obligatoria.");
        }
        if (normalizedDescription.length() > 150) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "La descripción no puede exceder 150 caracteres.");
        }

        List<String> storedNames = uploadStorage.store(images == null ? List.of() : images);
        DirtyUnitReport report = dirtyUnitReports.save(new DirtyUnitReport(
                employeeNumber.trim(), company.trim(), normalizedDescription, storedNames));
        return new ReportReceipt(report.getId(), "REGISTERED", report.getCreatedAt());
    }
}
