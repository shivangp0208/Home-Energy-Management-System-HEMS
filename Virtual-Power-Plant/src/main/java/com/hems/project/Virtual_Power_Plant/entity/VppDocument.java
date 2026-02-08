package com.hems.project.Virtual_Power_Plant.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vpp_document")
public class VppDocument {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vpp_id", nullable = false)
  private Vpp vpp;

  @Enumerated(EnumType.STRING)
  @Column(name = "document_type", nullable = false)
  private VppDocumentType documentType;

  @Column(name = "file_name", nullable = false)
  private String fileName;

  @Column(name = "url", nullable = false, length = 2000)
  private String url;

  @Column(name = "uploaded_at", nullable = false)
  private LocalDateTime uploadedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private VppDocumentStatus status;

  @Column(name = "notes")
  private String notes;
}


