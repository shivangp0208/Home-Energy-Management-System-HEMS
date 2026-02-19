package com.hems.project.Virtual_Power_Plant.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hems.project.Virtual_Power_Plant.entity.SiteGroup;

public interface SiteGroupRepository extends JpaRepository<SiteGroup, UUID> {
    boolean existsByGroupName(String groupName);
}
