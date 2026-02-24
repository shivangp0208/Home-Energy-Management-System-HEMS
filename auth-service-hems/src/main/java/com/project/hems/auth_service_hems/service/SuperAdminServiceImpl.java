package com.project.hems.auth_service_hems.service;

import com.project.hems.auth_service_hems.exception.RoleNotFoundException;
import com.project.hems.auth_service_hems.exception.UserHasAlreadyRoleException;
import com.project.hems.auth_service_hems.exception.UserHasNotRoleException;
import com.project.hems.auth_service_hems.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class SuperAdminServiceImpl implements SuperAdminService{


    @Value("${auth0.domain}")
    private String domain;

    @Value("${auth0.client-id}")
    private String clientId;

    @Value("${auth0.client-secret}")
    private String clientSecret;

    @Value("${auth0.audience}")
    private String audience;

    private final RestTemplate restTemplate;

    public void AssignRoleToUser(String email,String roleName){
        System.out.println("Auth0 Domain: " + domain);
        System.out.println("Auth0 Client ID: " + clientId);
        System.out.println("Auth0 Client Secret: " + clientSecret);
        System.out.println("Auth0 Audience: " + audience);

        roleName=roleName.toUpperCase();
        String managementToken=getManagementToken();

        String userId=getUserIdByEmail(email,managementToken);
        System.out.println("userId"+userId);
        String roleId= null;
        try {
            roleId = getRoleIdByName(roleName,managementToken);
        } catch ( RoleNotFoundException e) {
            throw new RuntimeException(e);
        }

        //if role alreay hase and fairthi same role assign karse toh we throw error
        if (userAlreadyHasRole(userId, roleId, managementToken)) {
            throw new UserHasAlreadyRoleException("User already has this role assigned");
        }

        assignRole(userId, roleId, managementToken);

    }

    private String getUserIdByEmail(String email, String managementToken) {
        String url=domain+"/api/v2/users-by-email?email="+email;

        HttpHeaders headers=new HttpHeaders();
        headers.setBearerAuth(managementToken);

        HttpEntity<?> entity=new HttpEntity<>(headers);

         ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
         List users=response.getBody();

         if(users==null || users.isEmpty()){
             throw new UserNotFoundException(email);
         }
         Map user=(Map) users.get(0);
         return user.get("user_id").toString();


    }

    private String getManagementToken() {
        String url=domain+"/oauth/token";

        Map<String,String> body=new HashMap<>();
        body.put("client_id",clientId);
        body.put("client_secret",clientSecret);
        body.put("audience",audience);
        body.put("grant_type","client_credentials");

        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String,String>> request=new HttpEntity<>(body,headers);


        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        return response.getBody().get("access_token").toString();

    }


    private String getRoleIdByName(String roleName, String token) {
        roleName=roleName.toUpperCase();
        String url = domain + "/api/v2/roles";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<List> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, List.class);

        List roles = response.getBody();
        System.out.println("roles"+roles);

        for (Object roleObj : roles) {
            Map role = (Map) roleObj;
            System.out.println("role"+role);
            if (roleName.equals(role.get("name"))) {
                return role.get("id").toString();
            }
        }

        throw new RoleNotFoundException(roleName);
    }

    private void assignRole(String userId, String roleId, String token) {

        String url =  domain +
                "/api/v2/users/" + userId + "/roles";

        Map<String, Object> body = new HashMap<>();
        body.put("roles", List.of(roleId));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        restTemplate.postForEntity(url, request, String.class);
    }

    public void removeRoleFromUser(String email, String roleName) {
        roleName=roleName.toUpperCase();

        String token = getManagementToken();

        String userId = getUserIdByEmail(email, token);

        String roleId;
        try{
             roleId = getRoleIdByName(roleName, token);
        } catch (RoleNotFoundException e) {
            throw new RuntimeException(e);
        }

        String url = "https://dev-0x5bg1uy1egiz4y0.us.auth0.com/api/v2/users/"
                + userId + "/roles";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, List<String>> body = Map.of(
                "roles", List.of(roleId)
        );

        HttpEntity<Map<String, List<String>>> entity =
                new HttpEntity<>(body, headers);
        if (userAlreadyHasRole(userId,roleId,token)){
            restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );
        }else{
            throw new UserHasNotRoleException("User does not have role: " + roleName);        }
    }

    public boolean userAlreadyHasRole(String userId, String roleId, String token) {

        String url = domain + "/api/v2/users/" + userId + "/roles";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<List> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, List.class);

        List roles = response.getBody();

        if (roles == null) return false;

        for (Object roleObj : roles) {
            Map role = (Map) roleObj;
            if (roleId.equals(role.get("id"))) {
                return true;
            }
        }

        return false;
    }
}
