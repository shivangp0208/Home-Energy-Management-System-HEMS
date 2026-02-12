package com.hems.project.Virtual_Power_Plant.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.hems.project.Virtual_Power_Plant.dto.VppOperationalStatus;
import com.hems.project.Virtual_Power_Plant.dto.VppVerificationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "virtual_power_plant")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Vpp {

    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "vpp_name",nullable = false)
    private String name;
    @Column(name = "vpp_region",nullable = false)
    private String region;

    private String country;

    private String city;

    //ahiya user ni detail store kariee ena karta we store in the auth service database
    //TODO:-
    //aa check karvu
    @Column(name = "auth_id")
    private String authUserId; // sub: auth0|65f9ab1234abcde

    private String email;
    @Column(name="total_solar_capacity",nullable = false)
    private Double totalSolarCapacityW;
    @Column(name = "total_battery_capacity",nullable=false)
    private Double totalBatteryCapacityWh;
    @Column(name = "available_battery_capacity",nullable = false)
    private Double availableBatteryCapacityWh;

    @Column(name = "current_live_generated_power")
    private Double currentLiveGeneratePowerW;

    @Column(name = "max_export_power_capacity")
    private Double maxExportPowerCapacityW;
    @Column(name = "max_import_power_capacity")
    private Double maxImportPowerCapacityW;
    @Column(name = "max_power_generation_capacity")
    private Double maxPowerGenerationCapacityW;

    @Column(name = "total_site")
    private Integer totalSites;

    @Enumerated(EnumType.STRING)
    private VppVerificationStatus verificationStatus;//jyare data submit kaare then 

    @Enumerated(EnumType.STRING)
    private VppOperationalStatus operationalStatus;
    
    @Column(name = "established_time")
    private LocalDateTime establishedTime;

    @Column(name = "last_updated_time")
    private LocalDateTime lastUpdatedTime;//data last kyare update karyo che eno time ..

    @Column(name = "verification_submited_time")
    private LocalDateTime submittedForVerificationAt;

    @Column(name = "verify_by")
    private String verifiedBy; //jene verify karyu hoy e vpp manager nu name..

    @Column(name = "verification_note")
    private String verificationNotes;      


}