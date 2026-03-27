package com.hems.project.admin_service.repository;


import com.hems.project.admin_service.entity.SiteGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SiteGroupRepository extends JpaRepository<SiteGroup, UUID> {
    boolean existsByGroupName(String groupName);
}
