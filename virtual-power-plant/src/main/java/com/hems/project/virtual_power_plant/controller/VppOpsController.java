package com.hems.project.virtual_power_plant.controller;

import com.hems.project.virtual_power_plant.dto.VppAccessStatus;
import com.hems.project.virtual_power_plant.service.VppOpsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;



//note:-
//aa controller em toh vpp manager ma hova joiee but mostly badhu vpp thi j call karvanu hatu
//so ama lakhyu che and entity ama manager ni nati banavi km ke manager service ma j rehva devi i
//so apde manager na claims ma add kar daisu ena accessible region and all e kaya region no che so based
//on that e access kari sakse direct aa endpoint call karine

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vpp/manager/vpps")
public class VppOpsController {

    private final VppOpsService vppOpsService;

    @PreAuthorize("hasAuthority('vppm:write')")
    @PatchMapping("/{vppId}/block")
    public ResponseEntity<?> block(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID vppId) {
        //jwt na claims mathi j joi laisu
        String region = jwt.getClaimAsString("https://hems.com/region");
        vppOpsService.setAccessStatus(region, vppId, VppAccessStatus.BLOCKED);
        return ResponseEntity.ok("Blocked");
    }


    //@PreAuthorize("hasRole('MANAGER')")
    @PreAuthorize("hasAuthority('vppm:write')")
    @PatchMapping("/{vppId}/unBlock")
    public ResponseEntity<?> unBlock(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID vppId) {
        String region = jwt.getClaimAsString("https://hems.com/region");
        vppOpsService.setAccessStatus(region, vppId, VppAccessStatus.ACTIVE);
        return ResponseEntity.ok("Activated");
    }

}
