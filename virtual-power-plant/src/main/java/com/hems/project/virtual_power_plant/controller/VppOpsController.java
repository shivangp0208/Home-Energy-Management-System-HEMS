package com.hems.project.virtual_power_plant.controller;

import com.hems.project.virtual_power_plant.dto.VppAccessStatus;
import com.hems.project.virtual_power_plant.service.VppOpsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;



//note:-
//aa controller em toh vpp manager ma hova joiee but mostly badhu vpp thi j call karvanu hatu
//so ama lakhyu che and entity ama manager ni nati banavi km ke manager service ma j rehva devi i
//so apde manager na claims ma add kar daisu ena accessible region and all e kaya region no che so based
//on that e access kari sakse direct aa endpoint call karine

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vpp/manager/vpps")
public class VppOpsController {

    private static final String REGION_CLAIM = "https://hems.com/region";
    private final VppOpsService vppOpsService;

    @PreAuthorize("hasAuthority('vppm:write')")
    @PatchMapping("/{vppId}/block")
    public ResponseEntity<Map<String, Object>> block(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID vppId) {

        String region = jwt.getClaimAsString(REGION_CLAIM);

        log.info("blocking vppId {} for region {}", vppId, region);

        vppOpsService.setAccessStatus(region, vppId, VppAccessStatus.BLOCKED);

        return ResponseEntity.ok(
                Map.of(
                        "message", "VPP blocked successfully",
                        "data", vppId
                )
        );
    }

    //@PreAuthorize("hasRole('MANAGER')")
    @PreAuthorize("hasAuthority('vppm:write')")
    @PatchMapping("/{vppId}/unblock")
    public ResponseEntity<Map<String, Object>> unBlock(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID vppId) {

        String region = jwt.getClaimAsString(REGION_CLAIM);

        log.info("unblocking vppId {} for region {}", vppId, region);

        vppOpsService.setAccessStatus(region, vppId, VppAccessStatus.ACTIVE);

        return ResponseEntity.ok(
                Map.of(
                        "message", "VPP activated successfully",
                        "data", vppId
                )
        );
    }

}
