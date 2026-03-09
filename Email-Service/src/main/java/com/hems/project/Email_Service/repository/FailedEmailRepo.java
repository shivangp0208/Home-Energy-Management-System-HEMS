package com.hems.project.Email_Service.repository;

import com.hems.project.Email_Service.entity.FailedEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FailedEmailRepo extends JpaRepository<FailedEmail, Long> {
}
