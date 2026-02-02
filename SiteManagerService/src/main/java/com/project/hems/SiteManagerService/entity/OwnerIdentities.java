package com.project.hems.SiteManagerService.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "owner_identity")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OwnerIdentities {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonBackReference
    private Owner owner;
    @Column(name = "auth_sub", nullable = false, unique = true, updatable = false)
    private String authSub;
    @Column(name = "provider", nullable = false, updatable = false)
    private String provider;
}
