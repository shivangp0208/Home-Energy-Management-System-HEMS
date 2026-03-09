package com.hems.project.ADMIN_SERVICE.entity;

import com.hems.project.ADMIN_SERVICE.dto.CasePriority;
import com.hems.project.ADMIN_SERVICE.dto.CaseSource;
import com.hems.project.ADMIN_SERVICE.dto.CaseStatus;
import com.hems.project.ADMIN_SERVICE.dto.CaseType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "case_tickets",
        indexes = {
                @Index(name = "idx_case_status", columnList = "status"),
                @Index(name = "idx_case_priority", columnList = "priority"),
                @Index(name = "idx_case_type", columnList = "type"),
                @Index(name = "idx_case_site", columnList = "siteId"),
                @Index(name = "idx_case_vpp", columnList = "vppId")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 30)
    private String caseNumber; // CASE-000123 this we generate manually from service and pass..

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CasePriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseSource source;

    @Column(nullable = false, length = 100)
    private String sourceService; // SITE-SERVICE, VPP-SERVICE, HEARTBEAT-SCHEDULER

    private UUID siteId;     // optional
    private UUID vppId;      // optional
    private UUID dispatchId; // optional

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Assignment
    private String assignedTo; // admin email or sub

    // Audit
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

/*
If SITE service sends:

CaseRaisedEventDto
        type = OFFLINE
siteId = 123
priority = CRITICAL

Admin service creates:

CASE-000234
type = OFFLINE
        priority = CRITICAL
status = NEW
        source = SYSTEM
sourceService = HEARTBEAT-SCHEDULER


 */

/*
A) Automatic creation (from SITE/VPP/SYSTEM)
SITE/VPP/SYSTEM detects issue
    |
    | publish CaseRaisedEventDto (RabbitMQ/Kafka)
    v
ADMIN-SERVICE consumes event
    |
    | build dedupeKey
    | check open case exists?
    |    |-- YES -> add CaseEvent ("duplicate issue")
    |    |-- NO  -> create CaseTicket (status NEW)
    |
    | create CaseEvent (CREATED)
    v
UI shows it in "New cases"
B) Manual creation (from Admin UI)
Admin UI -> POST /cases
    |
ADMIN-SERVICE validates siteId etc
    |
create CaseTicket + CaseEvent(CREATED)
    v
UI refresh
 */