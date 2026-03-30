package com.project.hems.site_manager_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController("check-token-is-present")
public class CheckToken {

    @GetMapping("/")
    public ResponseEntity<String> checkToken(@AuthenticationPrincipal Jwt jwt) {
        log.info("checkToken: GET req to check the token and fectch the sub claim from it");
        String sub = jwt.getSubject();
        log.debug("checkToken: retrieved sub claim from token = {}", sub);
        return ResponseEntity.ok(sub);
    }

}
