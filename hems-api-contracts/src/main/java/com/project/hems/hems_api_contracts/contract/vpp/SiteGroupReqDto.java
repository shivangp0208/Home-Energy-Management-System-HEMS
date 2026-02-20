package com.project.hems.hems_api_contracts.contract.vpp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SiteGroupReqDto {

    private UUID groupId;

    @NotBlank(message = "Group name must not be blank")
    @Size(min = 3, max = 100, message = "Group name must be between 3 and 100 characters")
    private String groupName;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Group type is required")
    private SiteGroupType groupType;

    private boolean groupStatus = true;

    @NotEmpty(message = "At least one site must be assigned to the group")
    private Set<@NonNull UUID> sitesInGroup = new HashSet<>();

    private LocalDateTime createdAt;

    @NotBlank(message = "CreatedBy must not be blank")
    @Size(max = 100, message = "CreatedBy cannot exceed 100 characters")
    private String createdBy;
}