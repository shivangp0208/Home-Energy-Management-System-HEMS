package com.hems.project.vpp_manager.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vpp_manager")
public class VppManager {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String email;

    private String region;      // Which region this manager controls

    private String country;

    private String phoneNumber;

    private boolean active;

    //@Enumerated(EnumType.STRING)
    //private ManagerRole role;   // SUPER_ADMIN / REGIONAL_MANAGER

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}


