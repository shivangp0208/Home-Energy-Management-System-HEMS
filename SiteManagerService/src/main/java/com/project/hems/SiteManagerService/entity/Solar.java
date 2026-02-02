package com.project.hems.SiteManagerService.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "solar")
@Data
public class Solar {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Positive(message = "panel capacity must be positive")
    @NotNull(message = "totalPanelCapacity cannot be null")
    private double totalPanelCapacityW;

    @NotNull(message = "inverterMaxCapacity cannot be null")
    @Positive(message = "inverter max capacity must be positive")
    private double inverterMaxCapacityW;

    @NotBlank(message = "solar panel orientation cannot be blank")
    private String orientation;

    @ManyToOne
    @JoinColumn(name = "site_id")
    @JsonBackReference // child side
    @NotNull(message = "site cannot be null")
    private Site site;

}
