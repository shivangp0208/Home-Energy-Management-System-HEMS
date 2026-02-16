package com.project.hems.hems_api_contracts.contract.site;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerIdentitiesDto {

    private UUID ownerIdentityId;

    @NotNull(message = "Owner reference is required")
    @JsonIgnore
    @ToString.Exclude
    private OwnerDto owner;

    @NotBlank(message = "Auth subject (sub) is required")
    @Size(max = 255, message = "Auth subject must not exceed 255 characters")
    private String authSub;

    @NotBlank(message = "Provider is required")
    @Size(max = 100, message = "Provider must not exceed 100 characters")
    private String provider;
}
