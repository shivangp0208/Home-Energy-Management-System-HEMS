package com.project.hems.dispatch_manager_service.service;

import com.project.hems.hems_api_contracts.contract.vpp.SignalForImport;

public interface VppRequirementConsumer {
     void vppRequirement(SignalForImport signalForImport);
}
