package com.project.hems.site_manager_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "owner_identity")
@Getter
@Setter
@ToString
public class OwnerIdentities {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID ownerIdentityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonBackReference
    private Owner owner;

    @Column(name = "auth_sub", nullable = false, unique = true, updatable = false)
    private String authSub;

    @Column(name = "provider", nullable = false, updatable = false)
    private String provider;
}
