package com.project.hems.SiteManagerService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.hems.SiteManagerService.entity.OwnerIdentities;

import java.util.UUID;

@Repository
public interface OwnerIdentityRepo extends JpaRepository<OwnerIdentities, UUID> {
    boolean existsByAuthSub(String authSub);
}
