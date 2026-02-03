package com.project.hems.SiteManagerService.controller;

import com.project.hems.SiteManagerService.dto.OwnerDto;
import com.project.hems.SiteManagerService.entity.Owner;
import com.project.hems.SiteManagerService.service.OwnerService;
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
// todo:-
// badha ma log lagava
public class OwnerController {

    private final OwnerService ownerService;

    @PostMapping("/create-owner")
    // @ResponseStatus(HttpStatus.CREATED)
    // @PreAuthorize("hasAuthority('SCOPE_site:write')")
    public ResponseEntity<OwnerDto> createOwner(
            @RequestBody Owner owner,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Request received to create owner ownerId ={}", owner.getId());
        log.info("subject:- " + jwt.getSubject());
        log.info("claim:- " + jwt.getClaims());
        String email = jwt.getClaimAsString("http://hems.com/email");
        log.info("email:- " + email);
        OwnerDto savedOwner = ownerService.createOwner(owner, jwt.getSubject(), email);
        return new ResponseEntity<>(savedOwner, HttpStatus.CREATED);
    }

    @GetMapping("/fetch-owner-by-id/{ownerId}")
    public ResponseEntity<OwnerDto> getOwner(@PathVariable UUID ownerId) {
        log.info("Request received to get owner and ownerId={}", ownerId);
        OwnerDto ownerDto = ownerService.getOwnerDetail(ownerId);
        return new ResponseEntity<>(ownerDto, HttpStatus.OK);
    }

    @GetMapping("/fetch-all-owner")
    // @PreAuthorize("hasAuthority('SCOPE_site:read')")
    public ResponseEntity<List<OwnerDto>> getAllOwner() {
        log.info("Request received to get all owner");
        List<OwnerDto> allOwner = ownerService.getAllOwnerDetail();
        return new ResponseEntity<>(allOwner, HttpStatus.OK);
    }

    @PutMapping("/update-owner")
    public ResponseEntity<OwnerDto> updateOwner(@RequestBody Owner owner) {
        log.info("Request received to update owner and ownerId={}", owner.getId());
        OwnerDto updatedOwner = ownerService.updateOwnerDetail(owner);
        return new ResponseEntity<>(updatedOwner, HttpStatus.OK);
    }

    @DeleteMapping("/delete-owner-by-id/{ownerId}")
    public ResponseEntity<OwnerDto> deleteOwner(@PathVariable UUID ownerId) {
        log.info("Request received to delete owner and ownerId={}", ownerId);
        ownerService.deleteOwner(ownerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
