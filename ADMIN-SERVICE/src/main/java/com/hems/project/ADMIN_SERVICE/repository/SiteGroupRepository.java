package com.hems.project.ADMIN_SERVICE.repository;


import com.hems.project.ADMIN_SERVICE.entity.SiteGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SiteGroupRepository extends JpaRepository<SiteGroup, UUID> {
    boolean existsByGroupName(String groupName);
}
