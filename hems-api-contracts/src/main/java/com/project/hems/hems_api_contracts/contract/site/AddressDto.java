package com.project.hems.hems_api_contracts.contract.site;
import lombok.Data;

import java.util.UUID;

@Data
public class AddressDto {
    private UUID id;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String county;
    private UUID siteId;
}
