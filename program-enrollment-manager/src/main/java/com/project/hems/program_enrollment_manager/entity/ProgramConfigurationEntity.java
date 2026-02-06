package com.project.hems.program_enrollment_manager.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Builder
@Entity
@Table(name = "program_configuration")
@Data
public class ProgramConfigurationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "config_id", nullable = false, updatable = false, unique = true)
    private UUID configId;

    @OneToOne
    @JoinColumn(name = "program_id", nullable = false)
    private ProgramEntity program;

    // @Type(JsonType.class)
    @Column(name = "program_description", columnDefinition = "jsonb")
    private String programDescription;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
