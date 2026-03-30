package com.hems.project.virtual_power_plant.service;

import com.hems.project.virtual_power_plant.dto.VppAccessStatus;
import com.hems.project.virtual_power_plant.entity.Vpp;
import com.hems.project.virtual_power_plant.exception.RegionNotMatchException;
import com.hems.project.virtual_power_plant.repository.VppRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VppOpsService {

        private final VppRepository vppRepository;

        public void setAccessStatus(String managerRegion, UUID vppId, VppAccessStatus status) {

            Vpp vpp = vppRepository.findById(vppId)
                    .orElseThrow(() -> new RuntimeException("VPP not found"));

            if (!vpp.getRegion().equalsIgnoreCase(managerRegion)) {
                throw new RegionNotMatchException(managerRegion,"not allowed you are in different region", HttpStatus.FORBIDDEN);
            }

            vpp.setAccessStatus(status);
            vppRepository.save(vpp);
        }
}


