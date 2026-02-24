package com.project.hems.auth_service_hems.service;

public interface SuperAdminService {

     void AssignRoleToUser(String email, String roleName);

     boolean userAlreadyHasRole(String userId, String roleId, String token);
}