package com.bancoppel.transporte.report;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dirty_unit_reports")
public class DirtyUnitReport {
    @Id
    private UUID id;

    @Column(nullable = false, length = 80)
    private String employeeNumber;

    @Column(nullable = false, length = 160)
    private String company;

    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String description;

    @ElementCollection
    @CollectionTable(name = "dirty_unit_report_images", joinColumns = @JoinColumn(name = "report_id"))
    @Column(name = "file_name", nullable = false, length = 255)
    private List<String> imageNames = new ArrayList<>();

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    protected DirtyUnitReport() {
    }

    public DirtyUnitReport(String employeeNumber, String company, String description, List<String> imageNames) {
        this.id = UUID.randomUUID();
        this.employeeNumber = employeeNumber;
        this.company = company;
        this.description = description;
        this.imageNames = new ArrayList<>(imageNames);
    }

    @PrePersist
    void assignCreationTime() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
