package com.project.hems.SiteManagerService.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.hems.hems_api_contracts.contract.program.AddProgramConfigInSite;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sites")
@Data
@ToString(exclude = { "owner", "solar", "battery", "address" })
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // private Long id;

    @Column(name = "vpp_id",nullable = true)
    private UUID vppId;


    @ManyToOne
    @JoinColumn(name = "owner")
    @JsonBackReference
    @NotNull(message = "owner entity number cannot be null")
    private Owner owner;

    @NotNull(message = "active status is required")
    private boolean isActive;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL) // one site have many solar panel
    @JsonManagedReference // parent side
    @NotEmpty(message = "solar entity number cannot be empty")
    private List<Solar> solar;

    @OneToOne(mappedBy = "site", cascade = CascadeType.ALL) // aa discuaa karvu ke ama One site have many battery hoi
                                                            // sake and
    // solar class ni under inverter max capacity nu su matlab??
    // and battery class ni under quantity?? battery info toh ek j battery ni hase
    // ne
    @JsonManagedReference
    @NotNull(message = "battery entity cannot be null")
    private Battery battery;

    @OneToOne(mappedBy = "site", cascade = CascadeType.ALL)
    @JsonManagedReference
    @NotNull(message = "address entity cannot be null")
    private Address address;

    // private List<UUID> enrollProgramIds;//ahiya apde direct List<Program> na kari
    // sakiee..
    //@ElementCollection
    //@Embedded
    //@CollectionTable(name = "site_programs", joinColumns = @JoinColumn(name = "site_id"))
    //@Column(name = "program")
    //@NotEmpty(message = "must be one program id is needed")
    //private List<AddProgramConfigInSite> enrollProgram=new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SiteProgram> enrollProgram = new ArrayList<>();
}
