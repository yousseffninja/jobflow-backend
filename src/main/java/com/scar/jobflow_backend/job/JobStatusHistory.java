package com.scar.jobflow_backend.job;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "job_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobStatusHistory {

    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private JobStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private JobStatus newStatus;

    private String note;

    @Column(name = "changed_at", updatable = false, nullable = false)
    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        this.changedAt = LocalDateTime.now();
    }
}