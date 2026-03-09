package com.project.hems.hems_api_contracts.contract.caseevent;

import com.hems.project.ADMIN_SERVICE.dto.CasePriority;
import com.hems.project.ADMIN_SERVICE.dto.CaseType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CaseRaisedEvent {

    private UUID eventId;


    private String sourceService;

    private CaseType type;

    private CasePriority priority;

    private UUID siteId;
    private UUID dispatchId;   // optional

    private String region;

    private String title;

    private String description;

    // siteId + ":" + type
    //like 1 hour site offline che so heartbeat is no found so 30 case open thai jase and it explode
    //so we make dedupekey
    private String dedupeKey;

    private LocalDateTime occurredAt;
}

/*
site vade..
CaseRaisedEvent event = CaseRaisedEvent.builder()
        .eventId(UUID.randomUUID())
        .sourceService("SITE_SERVICE")
        .type(CaseType.SITE_OFFLINE)
        .priority(CasePriority.CRITICAL)
        .siteId(siteId)
        .region("Surat")
        .title("Site Offline")
        .description("No heartbeat received for 6 minutes")
        .dedupeKey(siteId + ":SITE_OFFLINE")
        .occurredAt(LocalDateTime.now())
        .build();

rabbitTemplate.convertAndSend(
        "case.exchange",
        "case.raised",
        event

CaseRaisedEvent event = CaseRaisedEvent.builder()
        .eventId(UUID.randomUUID())
        .sourceService("VPP_SERVICE")
        .type(CaseType.COMMAND_FAILED)
        .priority(CasePriority.HIGH)
        .siteId(siteId)
        .dispatchId(dispatchId)
        .title("Dispatch Command Failed")
        .description("Timeout while sending dispatch command")
        .dedupeKey(dispatchId.toString())
        .occurredAt(LocalDateTime.now())
        .build();

);

how we calculate SLA
@RabbitListener(queues = "case.raised.queue")
public void handleCaseRaised(CaseRaisedEvent event) {

    // 1. Check if open case exists with same dedupeKey
    // 2. If exists -> add timeline event only
    // 3. If not -> create NEW case
    // 4. Calculate SLA
    // 5. Auto-assign based on rules
}
private LocalDateTime calculateSla(CaseType type, CasePriority priority) {

    return switch (priority) {
        case CRITICAL -> LocalDateTime.now().plusMinutes(15);
        case HIGH -> LocalDateTime.now().plusMinutes(30);
        case MEDIUM -> LocalDateTime.now().plusHours(2);
        case LOW -> LocalDateTime.now().plusHours(4);
    };
}
 */