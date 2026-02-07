package com.project.hems.program_enrollment_manager.entity;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.Type;

import com.project.hems.program_enrollment_manager.model.ProgramPriority;
import io.hypersistence.utils.hibernate.type.json.JsonType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
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

    @Type(JsonType.class)
    @Column(name = "program_description", columnDefinition = "jsonb")
    private Map<String, Object> programDescription;


    @Column(name = "program_priority")
    private ProgramPriority priority;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
}
