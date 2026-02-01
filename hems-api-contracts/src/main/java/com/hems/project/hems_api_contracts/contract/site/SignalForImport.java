package com.hems.project.hems_api_contracts.contract.site;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SignalForImport {
    private Double requiredPower;
    private String regionName;
}
