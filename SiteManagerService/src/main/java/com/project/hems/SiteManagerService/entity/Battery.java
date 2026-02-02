package com.project.hems.SiteManagerService.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "battery")
@Data
public class Battery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "site cannot be empty")
    @Min(value = 1, message = "minimum one battery is required for storing solar generated power")
    private int quantity;

    @NotNull(message = "battery capacity cannot be empty")
    @Positive(message = "capacity must be positive")
    private double capacityWh;

    @NotNull(message = "battery max output cannot be empty")
    @Positive(message = "maxOutput must be positive")
    private double maxOutputW;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "site_id")
    @NotNull(message = "site entity cannot be empty")
    private Site site;
}
