package com.project.hems.SiteManagerService.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sites")
@Getter
@Setter
@ToString
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, unique = true, nullable = false)
    private UUID siteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    private Owner owner;

    @Column(name = "site_status")
    private boolean siteStatus;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private List<Solar> solar = new ArrayList<>();

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private List<Battery> batteries = new ArrayList<>();

    @OneToOne(mappedBy = "site", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private Address address;

    @CollectionTable(name = "site_enroll_program_ids", joinColumns = @JoinColumn(name = "site_id"))
    @ElementCollection
    @Column(name = "program_id")
    private List<UUID> enrollProgramIds = new ArrayList<>();

    private boolean hasMeterActivated;

}
