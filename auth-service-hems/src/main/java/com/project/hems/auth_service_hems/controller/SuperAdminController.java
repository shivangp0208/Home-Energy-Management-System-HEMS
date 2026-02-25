package com.project.hems.auth_service_hems.controller;

import com.project.hems.auth_service_hems.dto.AdminRoleRequest;
import com.project.hems.auth_service_hems.service.SuperAdminServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final SuperAdminServiceImpl service;

    //@PreAuthorize("hasRole('SUPER_ADMIN')")
    @PreAuthorize("hasAuthority('site:write')")
    @PostMapping("/assign-role")
    public ResponseEntity<String> assignRole(@RequestBody @Valid AdminRoleRequest request){
        service.AssignRoleToUser(request.getEmail(),request.getRoleName());
        return ResponseEntity.ok("Role assigned successfully");

    }

    @PreAuthorize("hasAuthority('site:write')")
    @DeleteMapping("/remove-role")
    public String removeRole(@RequestParam String email,
                             @RequestParam String roleName) {

        service.removeRoleFromUser(email, roleName);
        return "Role removed successfully";
    }

}
