package com.hems.project.admin_service.repository;


import com.hems.project.admin_service.entity.SiteGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SiteGroupRepository extends JpaRepository<SiteGroup, UUID> {
    boolean existsByGroupName(String groupName);
}
