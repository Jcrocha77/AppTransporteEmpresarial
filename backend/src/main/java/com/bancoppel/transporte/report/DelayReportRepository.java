package com.bancoppel.transporte.report;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DelayReportRepository extends JpaRepository<DelayReport, UUID> {
}
