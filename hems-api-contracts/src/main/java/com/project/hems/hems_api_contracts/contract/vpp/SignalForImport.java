package com.project.hems.hems_api_contracts.contract.vpp;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SignalForImport {
    private String regionName;
    private HashMap<UUID,Double> requirement;
}

/**
  {
  "regionName": "SURAT",
  "requirement": {
    "22c76c6b-3615-4ca9-a0e9-5dd297f2e928": 5000.0,
    "5b70207a-fef7-4054-b328-80eb3b90184e": 3200.5,
    "8a4e5c46-f254-41d1-a787-1cb786c12326": 1500.0
  }
 */