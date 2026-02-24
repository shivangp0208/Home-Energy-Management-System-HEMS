package com.project.hems.hems_api_contracts.contract.site;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OwnerDto {

    private UUID ownerId;

    @NotBlank(message = "Owner name is required")
    @Size(max = 150, message = "Owner name must not exceed 150 characters")
    private String ownerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
    private String phoneNo;

    @JsonIgnore
    @ToString.Exclude
    private List<SiteDto> sites = new ArrayList<>();

    @Valid
    @JsonIgnore
    private List<OwnerIdentitiesDto> ownerIdentities = new ArrayList<>();
}
