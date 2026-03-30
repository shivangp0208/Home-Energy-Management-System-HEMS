package com.hems.project.admin_service.repository;

import com.hems.project.admin_service.dto.CasePriority;
import com.hems.project.admin_service.dto.CaseStatus;
import com.hems.project.admin_service.entity.CaseTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseTicketRepository extends JpaRepository<CaseTicket, UUID> {
    List<CaseTicket> findByStatus(CaseStatus status);

    List<CaseTicket> findBySiteId(UUID siteId);

    List<CaseTicket> findByVppId(UUID vppId);

    List<CaseTicket> findByStatusAndSiteId(CaseStatus status, UUID siteId);

    List<CaseTicket> findByStatusAndVppId(CaseStatus status, UUID vppId);

    Optional<CaseTicket> findByCaseNumber(String caseNumber);

    Page<CaseTicket> findByStatus(CaseStatus status, Pageable pageable);
    Page<CaseTicket> findByPriority(CasePriority priority, Pageable pageable);
    Page<CaseTicket> findByAssignedTo(String assignedTo, Pageable pageable);
    Page<CaseTicket> findBySiteId(UUID siteId, Pageable pageable);
    Page<CaseTicket> findByVppId(UUID vppId, Pageable pageable);

    Page<CaseTicket> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<CaseTicket> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<CaseTicket> findByAssignedToContainingIgnoreCase(String assignedTo, Pageable pageable);
    boolean existsBySiteIdAndStatusIn(UUID siteId, List<CaseStatus> statuses);
    Optional<CaseTicket> findFirstBySiteIdAndStatusIn(
            UUID siteId,
            List<CaseStatus> statuses
    );}
