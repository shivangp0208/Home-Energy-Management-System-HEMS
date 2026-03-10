package com.hems.project.ADMIN_SERVICE.service;

import com.hems.project.ADMIN_SERVICE.dto.*;
import com.hems.project.ADMIN_SERVICE.entity.CaseEvent;
import com.hems.project.ADMIN_SERVICE.entity.CaseTicket;
import com.hems.project.ADMIN_SERVICE.repository.CaseEventRepository;
import com.hems.project.ADMIN_SERVICE.repository.CaseTicketRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseTicketRepository caseTicketRepository;
    private final CaseEventRepository caseEventRepository;
    private final EntityManager entityManager;

    @Transactional
    public CaseCreatedResponseDto createCaseManully(CaseRaisedEventDto request) {

        CaseTicket caseTicket = CaseTicket.builder()
                .caseNumber(generateCaseNumber())
                .type(request.getType())
                .priority(request.getPriority())
                .status(CaseStatus.NEW)
                .source(request.getSource())
                .sourceService(request.getSourceService())
                .siteId(request.getSiteId())
                .vppId(request.getVppId())
                .dispatchId(request.getDispatchId())
                .title(request.getTitle())
                .description(request.getDescription())
                .assignedTo("admin") // TODO: take from logged-in admin (JWT sub/email)
                .build();

        caseTicket = caseTicketRepository.save(caseTicket);

        CaseEvent caseEvent = CaseEvent.builder()
                .caseId(caseTicket.getId())
                .eventType(CaseEventType.CREATED)
                .message("case created manually. Assigned to admin.")
                .actor("admin") // TODO: take from logged-in admin
                .build();

        caseEventRepository.save(caseEvent);

        return CaseCreatedResponseDto.builder()
                .id(caseTicket.getId())
                .caseNumber(caseTicket.getCaseNumber())
                .type(caseTicket.getType())
                .priority(caseTicket.getPriority())
                .status(caseTicket.getStatus())
                .source(caseTicket.getSource())
                .sourceService(caseTicket.getSourceService())
                .siteId(caseTicket.getSiteId())
                .vppId(caseTicket.getVppId())
                .dispatchId(caseTicket.getDispatchId())
                .title(caseTicket.getTitle())
                .description(caseTicket.getDescription())
                .assignedTo(caseTicket.getAssignedTo())
                .createdAt(caseTicket.getCreatedAt())
                .updatedAt(caseTicket.getUpdatedAt())
                .build();
    }

    public CaseStatus getCaseStatus(UUID caseId) {
        CaseTicket ticket = caseTicketRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("case not found: " + caseId));
        return ticket.getStatus();
    }

    public List<CaseCreatedResponseDto> getAllCases(CaseStatus status, UUID siteId, UUID vppId) {

        List<CaseTicket> tickets;

        if (status != null && siteId != null) {
            tickets = caseTicketRepository.findByStatusAndSiteId(status, siteId);
        } else if (status != null && vppId != null) {
            tickets = caseTicketRepository.findByStatusAndVppId(status, vppId);
        } else if (status != null) {
            tickets = caseTicketRepository.findByStatus(status);
        } else if (siteId != null) {
            tickets = caseTicketRepository.findBySiteId(siteId);
        } else if (vppId != null) {
            tickets = caseTicketRepository.findByVppId(vppId);
        } else {
            tickets = caseTicketRepository.findAll();
        }

        return tickets.stream().map(this::mapToResponse).toList();
    }

    private CaseCreatedResponseDto mapToResponse(CaseTicket caseTicket) {
        return CaseCreatedResponseDto.builder()
                .id(caseTicket.getId())
                .caseNumber(caseTicket.getCaseNumber())
                .type(caseTicket.getType())
                .priority(caseTicket.getPriority())
                .status(caseTicket.getStatus())
                .source(caseTicket.getSource())
                .sourceService(caseTicket.getSourceService())
                .siteId(caseTicket.getSiteId())
                .vppId(caseTicket.getVppId())
                .dispatchId(caseTicket.getDispatchId())
                .title(caseTicket.getTitle())
                .description(caseTicket.getDescription())
                .assignedTo(caseTicket.getAssignedTo())
                .createdAt(caseTicket.getCreatedAt())
                .updatedAt(caseTicket.getUpdatedAt())
                .build();
    }

    public List<CaseEvent> getCaseTimeline(UUID caseId) {
        if (!caseTicketRepository.existsById(caseId)) {
            throw new RuntimeException("Case not found: " + caseId);
        }

        return caseEventRepository.findByCaseIdOrderByCreatedAtAsc(caseId);
    }

    @Transactional
    public CaseCreatedResponseDto updateCaseStatus(UUID caseId,
                                                   UpdateCaseStatusRequest request) {

        CaseTicket ticket = caseTicketRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found: " + caseId));

        CaseStatus oldStatus = ticket.getStatus();
        CaseStatus newStatus = request.getNewStatus();

        if (oldStatus == newStatus) {
            throw new RuntimeException("Case already in status: " + newStatus);
        }

        // Update status
        ticket.setStatus(newStatus);
        caseTicketRepository.save(ticket);

        // Create event log
        CaseEvent event = CaseEvent.builder()
                .caseId(caseId)
                .eventType(CaseEventType.STATUS_CHANGED)
                .message("Status changed from " + oldStatus + " → " + newStatus)
                .actor(request.getActor())
                .build();

        caseEventRepository.save(event);

        return mapToResponse(ticket);
    }

    @Transactional
    public CaseCreatedResponseDto transferCase(UUID caseId, TransferCaseRequestDto request) {

        CaseTicket ticket = caseTicketRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("case not found: " + caseId));

        String oldAssignee = ticket.getAssignedTo();
        String newAssignee = request.getNewAssignee();

        if (newAssignee.equalsIgnoreCase(oldAssignee)) {
            throw new RuntimeException("case already assigned to: " + newAssignee);
        }

        // update assignedTo
        ticket.setAssignedTo(newAssignee);
        caseTicketRepository.save(ticket);

        // create audit event
        String msg = "transferred case from " + oldAssignee + " → " + newAssignee;
        if (request.getReason() != null && !request.getReason().isBlank()) {
            msg += " | reason: " + request.getReason();
        }

        CaseEvent event = CaseEvent.builder()
                .caseId(caseId)
                .eventType(CaseEventType.ASSIGNED)
                .message(msg)
                .actor(request.getActor())
                .build();

        caseEventRepository.save(event);

        return mapToResponse(ticket);
    }


    @Transactional
    public void addComment(UUID caseId, AddCommentRequestDto request) {

        if (!caseTicketRepository.existsById(caseId)) {
            throw new RuntimeException("Case not found: " + caseId);
        }

        CaseEvent event = CaseEvent.builder()
                .caseId(caseId)
                .eventType(CaseEventType.COMMENT_ADDED)
                .message(request.getComment())
                .actor(request.getActor())
                .build();

        caseEventRepository.save(event);
    }



    // ---------------- READ ----------------

    public CaseCreatedResponseDto getCaseById(UUID caseId) {
        CaseTicket ticket = caseTicketRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found: " + caseId));
        return mapToResponse(ticket);
    }



    // ---------------- COMMENTS ----------------



    // ---------------- REOPEN / CLOSE ----------------

    @Transactional
    public CaseCreatedResponseDto reopenCase(UUID caseId, String actor, String reason) {
        CaseTicket ticket = caseTicketRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found: " + caseId));

        CaseStatus old = ticket.getStatus();

        // allow reopen from RESOLVED or CLOSED
        if (!(old == CaseStatus.RESOLVED || old == CaseStatus.CLOSED)) {
            throw new RuntimeException("Reopen allowed only from RESOLVED/CLOSED. Current: " + old);
        }

        ticket.setStatus(CaseStatus.IN_PROGRESS);
        ticket = caseTicketRepository.save(ticket);

        String msg = "Case reopened " + old + " -> IN_PROGRESS";
        if (reason != null && !reason.isBlank()) msg += " | Reason: " + reason;

        caseEventRepository.save(CaseEvent.builder()
                .caseId(caseId)
                .eventType(CaseEventType.STATUS_CHANGED)
                .message(msg)
                .actor(defaultActor(actor))
                .build());

        return mapToResponse(ticket);
    }

    @Transactional
    public CaseCreatedResponseDto closeCase(UUID caseId, CloseCaseRequestDto req) {
        CaseTicket ticket = caseTicketRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found: " + caseId));

        CaseStatus old = ticket.getStatus();

        // Usually you close from RESOLVED, but allow from IN_PROGRESS if you want
        ticket.setStatus(CaseStatus.CLOSED);
        ticket = caseTicketRepository.save(ticket);

        caseEventRepository.save(CaseEvent.builder()
                .caseId(caseId)
                .eventType(CaseEventType.STATUS_CHANGED)
                .message("Status changed " + old + " -> CLOSED")
                .actor(defaultActor(req.getActor()))
                .build());

        caseEventRepository.save(CaseEvent.builder()
                .caseId(caseId)
                .eventType(CaseEventType.COMMENT_ADDED)
                .message("Resolution: " + req.getResolutionSummary())
                .actor(defaultActor(req.getActor()))
                .build());

        return mapToResponse(ticket);
    }


    public Page<CaseCreatedResponseDto> listCases(
            CaseStatus status,
            CasePriority priority,
            UUID siteId,
            UUID vppId,
            String assignedTo,
            Pageable pageable
    ) {
        Page<CaseTicket> page;

        // simple priority order for filters
        if (status != null) {
            page = caseTicketRepository.findByStatus(status, pageable);
        } else if (priority != null) {
            page = caseTicketRepository.findByPriority(priority, pageable);
        } else if (siteId != null) {
            page = caseTicketRepository.findBySiteId(siteId, pageable);
        } else if (vppId != null) {
            page = caseTicketRepository.findByVppId(vppId, pageable);
        } else if (assignedTo != null && !assignedTo.isBlank()) {
            page = caseTicketRepository.findByAssignedTo(assignedTo, pageable);
        } else {
            page = caseTicketRepository.findAll(pageable);
        }

        return page.map(this::mapToResponse);
    }

//    public Page<CaseCreatedResponseDto> search(
//            String caseNumber,
//            String title,
//            String assignedTo,
//            UUID siteId,
//            LocalDateTime from,
//            LocalDateTime to,
//            Pageable pageable
//    ) {
//        // Most specific first
//        if (caseNumber != null && !caseNumber.isBlank()) {
//            return caseTicketRepository.findByCaseNumber(caseNumber)
//                    .map(ticket -> new org.springframework.data.domain.PageImpl<>(
//                            java.util.List.of(mapToResponse(ticket)),
//                            pageable,
//                            1
//                    ))
//                    .orElseGet(() -> org.springframework.data.domain.Page.empty(pageable));
//        }
//
//        if (siteId != null) {
//            return caseTicketRepository.findBySiteId(siteId, pageable).map(this::mapToResponse);
//        }
//        if (from != null && to != null) {
//            return caseTicketRepository.findByCreatedAtBetween(from, to, pageable).map(this::mapToResponse);
//        }
//
//        if (title != null && !title.isBlank()) {
//            return caseTicketRepository.findByTitleContainingIgnoreCase(title, pageable).map(this::mapToResponse);
//        }
//
//        if (assignedTo != null && !assignedTo.isBlank()) {
//            return caseTicketRepository.findByAssignedToContainingIgnoreCase(assignedTo, pageable).map(this::mapToResponse);
//        }
//
//        return Page.empty(pageable);
//    }

    public Page<CaseCreatedResponseDto> myCases(String assignedTo, Pageable pageable) {
        // In real system take from JWT:
        // String me = SecurityContextHolder.getContext().getAuthentication().getName();
        // return caseTicketRepository.findByAssignedTo(me, pageable).map(this::mapToResponse);

        if (assignedTo == null || assignedTo.isBlank()) {
            throw new RuntimeException("assignedTo required for now (later take from JWT)");
        }
        return caseTicketRepository.findByAssignedTo(assignedTo, pageable).map(this::mapToResponse);
    }

    // ---------------- DASHBOARD ----------------

    public CaseDashboardSummaryDto dashboardSummary() {
        // Quick version (counts via repository + filtering in DB is better with custom queries)
        long total = caseTicketRepository.count();

        long newCount = caseTicketRepository.findByStatus(CaseStatus.NEW, Pageable.unpaged()).getTotalElements();
        long inProgressCount = caseTicketRepository.findByStatus(CaseStatus.IN_PROGRESS, Pageable.unpaged()).getTotalElements();
        long resolvedCount = caseTicketRepository.findByStatus(CaseStatus.RESOLVED, Pageable.unpaged()).getTotalElements();
        long closedCount = caseTicketRepository.findByStatus(CaseStatus.CLOSED, Pageable.unpaged()).getTotalElements();

        long criticalCount = caseTicketRepository.findByPriority(CasePriority.CRITICAL, Pageable.unpaged()).getTotalElements();
        long highCount = caseTicketRepository.findByPriority(CasePriority.HIGH, Pageable.unpaged()).getTotalElements();

        return CaseDashboardSummaryDto.builder()
                .total(total)
                .newCount(newCount)
                .inProgressCount(inProgressCount)
                .resolvedCount(resolvedCount)
                .closedCount(closedCount)
                .criticalCount(criticalCount)
                .highCount(highCount)
                .build();
    }

    // ---------------- HELPERS ----------------

    private String defaultActor(String actor) {
        return (actor == null || actor.isBlank()) ? "system" : actor;
    }


    @Transactional
    public String generateCaseNumber() {
        System.out.println(
                entityManager.createNativeQuery("select current_database()").getSingleResult()
        );
        Long nextVal = ((Number) entityManager
                .createNativeQuery("SELECT nextval('public.case_number_seq')").getSingleResult())
                .longValue();

        return String.format("CASE-%06d", nextVal);
    }
    @Transactional
    public void addEvent(CaseEvent event){
        caseEventRepository.save(event);
    }
}