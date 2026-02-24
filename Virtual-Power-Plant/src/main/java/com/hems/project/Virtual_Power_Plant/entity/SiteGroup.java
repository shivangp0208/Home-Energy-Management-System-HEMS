package com.hems.project.Virtual_Power_Plant.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.project.hems.hems_api_contracts.contract.vpp.SiteGroupType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "site_groups")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SiteGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID groupId; // Or String if using UUIDs like "GRP-123"

    @Column(unique = true, nullable = false)
    private String groupName; // e.g., "North Ahmedabad Feeder"

    private String description; // e.g., "All sites connected to Substation A"

    @Enumerated(EnumType.STRING)
    private SiteGroupType groupType; // GEOGRAPHIC, DEMOGRAPHIC, CUSTOM, TRANSFORMER

    @Column(name = "group_status", nullable = false)
    private boolean groupStatus = true;

    // The Many-to-Many Relationship
    // This automatically creates a junction table (e.g., group_site_mapping)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "sites_in_group", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "sites", nullable = false)
    private Set<UUID> sitesInGroup = new HashSet<>();

    // Audit Fields
    @CreationTimestamp
    private LocalDateTime createdAt;

    private String createdBy; // Which Admin created this group?
}
