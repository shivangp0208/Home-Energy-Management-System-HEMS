package com.hems.project.Vpp_Manager.service;

import org.springframework.stereotype.Service;

import com.hems.project.Vpp_Manager.entity.VirtualPowerPlant;
import com.hems.project.Vpp_Manager.repository.VirtualPowerPlantRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VirtualPowerPlantService {
    
    private final VirtualPowerPlantRepo virtualPowerPlantRepo;

    public String saveVppData(VirtualPowerPlant virtualPowerPlant){
        virtualPowerPlantRepo.save(virtualPowerPlant);
        return "successfull";
    }
}
