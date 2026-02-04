package com.project.hems.SiteManagerService.controller;

import com.project.hems.SiteManagerService.entity.Owner;
import com.project.hems.SiteManagerService.service.OwnerService;
import com.project.hems.hems_api_contracts.contract.site.OwnerDto;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/owner")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Owner APIs", description = "Create, Update, Delete Owner")
public class OwnerController {

    private final OwnerService ownerService;

    @PostMapping("/create-owner")
    // @ResponseStatus(HttpStatus.CREATED)
    // @PreAuthorize("hasAuthority('SCOPE_site:write')")
    public ResponseEntity<OwnerDto> createOwner(
            @RequestBody Owner owner,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("createOwner: POST req to create owner with owner detail = {}", owner);

        String email = jwt.getClaimAsString("http://hems.com/email");
        log.debug("createOwner: retrieved subject = {} and email = {}", jwt.getSubject(), email);

        OwnerDto savedOwner = ownerService.createOwner(owner, jwt.getSubject(), email);

        return new ResponseEntity<>(savedOwner, HttpStatus.CREATED);
    }

    @GetMapping("/fetch-owner-by-id/{ownerId}")
    public ResponseEntity<OwnerDto> getOwner(@PathVariable UUID ownerId) {
        log.info("getOwner: GET req to fetch the owner detail with given owner id = {}", ownerId);

        OwnerDto ownerDto = ownerService.getOwnerDetail(ownerId);

        return new ResponseEntity<>(ownerDto, HttpStatus.OK);
    }

    @GetMapping("/fetch-all-owner")
    // @PreAuthorize("hasAuthority('SCOPE_site:read')")
    public ResponseEntity<List<OwnerDto>> getAllOwner() {
        log.info("getAllOwner: GET req to fetch all owners list available in DB");

        List<OwnerDto> allOwner = ownerService.getAllOwnerDetail();

        return new ResponseEntity<>(allOwner, HttpStatus.OK);
    }

    @PutMapping("/update-owner")
    public ResponseEntity<OwnerDto> updateOwner(@RequestBody Owner owner) {
        log.info("updateOwner: PUT req to update owner details with given detail = {}", owner);

        OwnerDto updatedOwner = ownerService.updateOwnerDetail(owner);

        return new ResponseEntity<>(updatedOwner, HttpStatus.OK);
    }

    @DeleteMapping("/delete-owner-by-id/{ownerId}")
    public ResponseEntity<OwnerDto> deleteOwner(@PathVariable UUID ownerId) {
        log.info("deleteOwner: DELETE req to delete the owner with given owner id = {}", ownerId);

        ownerService.deleteOwner(ownerId);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
