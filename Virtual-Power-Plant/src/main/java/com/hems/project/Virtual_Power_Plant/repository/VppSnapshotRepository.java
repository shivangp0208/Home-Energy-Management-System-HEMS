package com.hems.project.Virtual_Power_Plant.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hems.project.Virtual_Power_Plant.entity.VppSnapshotEntity;

public interface VppSnapshotRepository
        extends JpaRepository<VppSnapshotEntity, UUID> {

    // Latest snapshot per VPP (Postgres DISTINCT ON)
    @Query(value = """
        SELECT DISTINCT ON (vpp_id) *
        FROM vpp_snapshot
        ORDER BY vpp_id, timestamp DESC
        """, nativeQuery = true)
    List<VppSnapshotEntity> findLatestSnapshotsPerVpp();
}
