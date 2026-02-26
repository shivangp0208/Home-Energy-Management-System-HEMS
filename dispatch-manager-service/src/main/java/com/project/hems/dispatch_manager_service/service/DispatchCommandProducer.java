package com.project.hems.dispatch_manager_service.service;

import com.project.hems.hems_api_contracts.contract.vpp.DispatchEventDto;

public interface DispatchCommandProducer {
     void processBulkDispatchEvent(DispatchEventDto bulkEvent);
}
