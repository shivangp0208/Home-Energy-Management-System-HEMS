package com.hems.project.Virtual_Power_Plant.service;

import com.hems.project.Virtual_Power_Plant.dto.VppAccessStatus;
import com.hems.project.Virtual_Power_Plant.entity.Vpp;
import com.hems.project.Virtual_Power_Plant.repository.VppRepository;
import lombok.RequiredArgsConstructor;
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
                throw new RuntimeException("Not allowed you are in different region");
            }

            vpp.setAccessStatus(status);
            vppRepository.save(vpp);
        }
}


