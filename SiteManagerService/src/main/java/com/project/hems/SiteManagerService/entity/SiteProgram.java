package com.project.hems.SiteManagerService.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.hems.hems_api_contracts.contract.program.ProgramPriority;
import com.project.hems.hems_api_contracts.contract.program.ProgramStatus;
import com.project.hems.hems_api_contracts.contract.program.ProgramType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "site_programs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID programId;

    private String programName;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    private ProgramPriority programPriority;

    @Enumerated(EnumType.STRING)
    private ProgramType programType;

    @Enumerated(EnumType.STRING)
    private ProgramStatus programStatus;

    @Type(JsonType.class)
    @Column(name = "program_description", columnDefinition = "json")
    private Map<String, Object> programDescription;

    @ManyToOne
    @JoinColumn(name = "site_id")
    @JsonBackReference
    private Site site;
}