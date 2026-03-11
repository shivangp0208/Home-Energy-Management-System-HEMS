package com.project.hems.SiteManagerService.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.hems.SiteManagerService.entity.Owner;
import com.project.hems.SiteManagerService.entity.Site;

import java.util.List;
import java.util.UUID;

@Repository
public interface SiteRepo extends JpaRepository<Site, UUID> {
    void deleteByOwner(Owner owner);

    List<Site> findByAddress_City(String city);

    @Query("SELECT DISTINCT a.city FROM Address a")
    List<String> findAllRegion();

    @Query("""
            SELECT s FROM Site s
            WHERE (:cursor IS NULL OR s.id> :cursor)
            ORDER BY s.id ASC
            """)
    public List<Site> fetchNextPage(@Param("cursor") UUID cursor, Pageable pagable);

    @Query("""
            SELECT s
            FROM Site s
            WHERE :programId MEMBER OF s.enrollProgramIds
            """)
    public List<Site> findAllSitesByEnrollProgramIds(@Param("programId") UUID programId);

    @Query("select s.siteId from Site s where s.hasMeterActivated = :hasMeterActivated")
    List<UUID> findAllSiteIdsByHasMeterActivated(boolean hasMeterActivated);
}