package com.project.hems.SiteManagerService.controller;

import com.project.hems.SiteManagerService.service.OwnerServiceImpl;
import com.project.hems.hems_api_contracts.contract.site.OwnerDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "owner apis", description = "create, update, delete, and fetch owner details")
public class OwnerController {

    private final OwnerServiceImpl ownerService;

    @Operation(summary = "create owner", description = "create a new owner using request body and return the saved owner details")
    @ApiResponse(responseCode = "201", description = "owner created successfully")
    @ApiResponse(responseCode = "400", description = "invalid owner data")
    @PostMapping("/create-owner")
    // @ResponseStatus(HttpStatus.CREATED)
    // @PreAuthorize("hasAuthority('SCOPE_site:write')")
    public ResponseEntity<OwnerDto> createOwner(
            @RequestBody OwnerDto ownerDto,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("createOwner: POST req to create owner with owner detail = {}", ownerDto);

        String email = jwt.getClaimAsString("http://hems.com/email");
        log.debug("createOwner: retrieved subject = {} and email = {}", jwt.getSubject(), email);

        OwnerDto savedOwner = ownerService.createOwner(ownerDto, jwt.getSubject(), email);

        return new ResponseEntity<>(savedOwner, HttpStatus.CREATED);
    }

    @Operation(summary = "fetch owner by id", description = "retrieve owner details by given owner id")
    @ApiResponse(responseCode = "200", description = "owner fetched successfully")
    @ApiResponse(responseCode = "404", description = "owner not found")
    @GetMapping("/fetch-owner-by-id/{ownerId}")
    public ResponseEntity<OwnerDto> getOwner(@PathVariable UUID ownerId) {
        log.info("getOwner: GET req to fetch the owner detail with given owner id = {}", ownerId);

        OwnerDto ownerDto = ownerService.getOwnerDetail(ownerId);

        return new ResponseEntity<>(ownerDto, HttpStatus.OK);
    }

    @Operation(summary = "fetch all owners", description = "retrieve a list of all owners available in the database")
    @ApiResponse(responseCode = "200", description = "owners list fetched successfully")
    @GetMapping("/fetch-all-owner")
    // @PreAuthorize("hasAuthority('SCOPE_site:read')")
    public ResponseEntity<List<OwnerDto>> getAllOwner() {
        log.info("getAllOwner: GET req to fetch all owners list available in DB");

        List<OwnerDto> allOwner = ownerService.getAllOwnerDetail();

        return new ResponseEntity<>(allOwner, HttpStatus.OK);
    }

    @Operation(summary = "update owner", description = "update owner details and return the updated owner object")
    @ApiResponse(responseCode = "200", description = "owner updated successfully")
    @ApiResponse(responseCode = "404", description = "owner not found")
    @PutMapping("/update-owner/{ownerId}")
    public ResponseEntity<OwnerDto> updateOwner(
            @PathVariable(name = "ownerId", required = true) UUID ownerId,
            @RequestBody @Valid OwnerDto ownerDto) {
        log.info(
                "updateOwner: PUT req to update owner details with given detail ownerId = {} and updated ownerDto = {}",
                ownerId, ownerDto);

        OwnerDto updatedOwner = ownerService.updateOwnerDetail(ownerId, ownerDto);

        return new ResponseEntity<>(updatedOwner, HttpStatus.OK);
    }

    @Operation(summary = "delete owner by id", description = "delete the owner with the given owner id")
    @ApiResponse(responseCode = "200", description = "owner deleted successfully")
    @ApiResponse(responseCode = "404", description = "owner not found")
    @DeleteMapping("/delete-owner-by-id/{ownerId}")
    public ResponseEntity<OwnerDto> deleteOwner(@PathVariable UUID ownerId) {
        log.info("deleteOwner: DELETE req to delete the owner with given owner id = {}", ownerId);

        ownerService.deleteOwner(ownerId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
