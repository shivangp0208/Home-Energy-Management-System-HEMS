package com.hems.project.ADMIN_SERVICE.entity;

import com.hems.project.ADMIN_SERVICE.dto.CaseEventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "case_events",
        indexes = {
                @Index(name="idx_case_events_caseId", columnList = "caseId"),
                @Index(name="idx_case_events_createdAt", columnList = "createdAt")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID caseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseEventType eventType;

    @Column(columnDefinition = "TEXT")
    private String message;   // "Assigned to admin1@x.com" / "Status changed NEW->IN_PROGRESS"

    private String actor;     // "system" or admin email/sub

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}