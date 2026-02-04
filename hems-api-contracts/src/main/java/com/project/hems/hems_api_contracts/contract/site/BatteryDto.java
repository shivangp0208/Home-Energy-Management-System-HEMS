package com.project.hems.hems_api_contracts.contract.site;
import lombok.Data;

import java.util.UUID;
@Data
public class BatteryDto {
    private UUID id;
    private int quantity;
    private double capacityWh;
    private double maxOutputW;
    private UUID siteId;
}
