package com.bancoppel.transporte.report;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirtyUnitReportRepository extends JpaRepository<DirtyUnitReport, UUID> {
}
