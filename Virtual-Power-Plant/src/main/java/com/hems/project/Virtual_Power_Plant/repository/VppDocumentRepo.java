package com.hems.project.Virtual_Power_Plant.repository;

import com.hems.project.Virtual_Power_Plant.entity.VppDocument;
import com.hems.project.Virtual_Power_Plant.entity.VppDocumentStatus;
import com.hems.project.Virtual_Power_Plant.entity.VppDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VppDocumentRepo extends JpaRepository<VppDocument, UUID> {
    List<VppDocument> findByVpp_Id(UUID vppId);

    List<VppDocument> findByVpp_IdAndStatus(UUID vppId, VppDocumentStatus status);

    List<VppDocument> findByStatus(VppDocumentStatus status);

    List<VppDocument> findByVpp_IdAndDocumentType(UUID vppId,VppDocumentType documentType);

    void deleteByVpp_IdAndDocumentType(UUID vppId, VppDocumentType documentType);

}
