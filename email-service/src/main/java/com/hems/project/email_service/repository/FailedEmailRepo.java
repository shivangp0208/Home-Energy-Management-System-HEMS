package com.hems.project.email_service.repository;

import com.hems.project.email_service.entity.FailedEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FailedEmailRepo extends JpaRepository<FailedEmail, Long> {
}
