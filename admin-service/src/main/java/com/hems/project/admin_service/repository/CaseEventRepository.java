package com.hems.project.admin_service.repository;

import com.hems.project.admin_service.dto.CasePriority;
import com.hems.project.admin_service.dto.CaseStatus;
import com.hems.project.admin_service.entity.CaseEvent;
import com.hems.project.admin_service.entity.CaseTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaseEventRepository extends JpaRepository<CaseEvent, UUID> {
    List<CaseEvent> findByCaseIdOrderByCreatedAtAsc(UUID caseId);

}
