package com.project.hems.auth_service_hems.controller;

import com.project.hems.auth_service_hems.dto.AdminRoleRequest;
import com.project.hems.auth_service_hems.service.SuperAdminServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final SuperAdminServiceImpl service;

    //@PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "assign role to user",
            description = "assign a role to a user using email"
    )
    @ApiResponse(responseCode = "200", description = "role assigned successfully")
    @PreAuthorize("hasAuthority('site:write')")
    @PostMapping("/assign-role")
    public ResponseEntity<String> assignRole(@RequestBody @Valid AdminRoleRequest request){

        log.info("assigning role {} to user {}", request.getRoleName(), request.getEmail());

        try {
            service.AssignRoleToUser(request.getEmail(), request.getRoleName());
            log.info("role assigned successfully to user {}", request.getEmail());
            return ResponseEntity.ok("role assigned successfully");

        } catch (Exception e) {
            log.error("error assigning role to user {}: {}", request.getEmail(), e.getMessage(), e);
            throw e;
        }
    }


    @Operation(
            summary = "remove role from user",
            description = "remove a role from a user using email"
    )
    @ApiResponse(responseCode = "200", description = "role removed successfully")
    @PreAuthorize("hasAuthority('site:write')")
    @DeleteMapping("/remove-role")
    public ResponseEntity<String> removeRole(
            @RequestParam String email,
            @RequestParam String roleName) {

        log.info("removing role {} from user {}", roleName, email);

        try {
            service.removeRoleFromUser(email, roleName);
            log.info("role removed successfully from user {}", email);
            return ResponseEntity.ok("role removed successfully");

        } catch (Exception e) {
            log.error("error removing role from user {}: {}", email, e.getMessage(), e);
            throw e;
        }
    }

}
