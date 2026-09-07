package com.scar.jobflow_backend.ai;

import com.scar.jobflow_backend.job.Job;
import com.scar.jobflow_backend.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_generations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiGeneration {

    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AiGenerationType type;

    @Column(name = "input_snapshot", columnDefinition = "TEXT")
    private String inputSnapshot;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String result;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
