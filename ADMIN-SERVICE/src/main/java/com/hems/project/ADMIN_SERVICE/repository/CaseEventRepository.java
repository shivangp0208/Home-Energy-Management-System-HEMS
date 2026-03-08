package com.hems.project.ADMIN_SERVICE.repository;

import com.hems.project.ADMIN_SERVICE.dto.CasePriority;
import com.hems.project.ADMIN_SERVICE.dto.CaseStatus;
import com.hems.project.ADMIN_SERVICE.entity.CaseEvent;
import com.hems.project.ADMIN_SERVICE.entity.CaseTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaseEventRepository extends JpaRepository<CaseEvent, UUID> {
    List<CaseEvent> findByCaseIdOrderByCreatedAtAsc(UUID caseId);

}
