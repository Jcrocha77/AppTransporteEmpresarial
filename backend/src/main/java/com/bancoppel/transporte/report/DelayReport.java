package com.bancoppel.transporte.report;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "delay_reports")
public class DelayReport {
    @Id
    private UUID id;

    @Column(nullable = false, length = 80)
    private String employeeNumber;

    @Column(nullable = false, length = 160)
    private String company;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private DelayReason reason;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    protected DelayReport() {
    }

    public DelayReport(String employeeNumber, String company, DelayReason reason) {
        this.id = UUID.randomUUID();
        this.employeeNumber = employeeNumber;
        this.company = company;
        this.reason = reason;
    }

    @PrePersist
    void assignCreationTime() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
