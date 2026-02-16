package com.project.hems.hems_api_contracts.contract.site;

import lombok.Data;
import lombok.ToString;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@ToString
public class AddressDto {

    private UUID addressId;

    @NotBlank(message = "Address line 1 is required")
    @Size(max = 255, message = "Address line 1 must not exceed 255 characters")
    private String addressLine1;

    @Size(max = 255, message = "Address line 2 must not exceed 255 characters")
    private String addressLine2;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Postal code must be a valid 6-digit number")
    private String postalCode;

    @NotBlank(message = "country is required")
    @Size(max = 100, message = "country must not exceed 100 characters")
    private String country;

    @JsonIgnore
    private SiteDto site;
}
