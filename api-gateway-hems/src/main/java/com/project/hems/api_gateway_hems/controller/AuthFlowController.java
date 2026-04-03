//package com.project.hems.api_gateway_hems.controller;
//
//import com.project.hems.api_gateway_hems.service.impl.Auth0ManagementServiceImpl;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//import reactor.core.publisher.Mono;
//
//import java.util.Map;
//
//@RequiredArgsConstructor
//@RestController
//@RequestMapping("/auth")
//public class AuthFlowController {
//
//    private final Auth0ManagementServiceImpl mgmt;
//
//   // public AuthFlowController(Auth0ManagementServiceImpl mgmt) {
//     //   this.mgmt = mgmt;
//    //}
//
//    @PostMapping("/check-email-and-handle")
//    public Mono<Map<String, Object>> check(@RequestBody Map<String, String> body) {
//        String email = body.get("email");
//
//        return mgmt.isSocialOnlyUser(email)
//                .flatMap(isSocialOnly -> {
//                    if (!isSocialOnly) {
//                        return Mono.just(Map.of(
//                                "status", "OK",
//                                "message", "Proceed with normal DB login"
//                        ));
//                    }
//
//                    // ✅ FIX: this must be the full C3 flow (create DB identity + link + email)
//                    return mgmt.sendPasswordSetupForSocialUser(email)
//                            .thenReturn(Map.of(
//                                    "status", "PASSWORD_SETUP_EMAIL_SENT",
//                                    "message", "This email is registered with Google/GitHub. We sent a password setup email. Check inbox/spam."
//                            ));
//                });
//    }
//
//    // optional debug endpoint if you want
//    @PostMapping("/debug-c3")
//    public Mono<Map<String, String>> debug(@RequestBody Map<String, String> body) {
//        String email = body.get("email");
//        return mgmt.sendPasswordSetupForSocialUser(email)
//                .thenReturn(Map.of("status", "OK", "message", "[C3] triggered for " + email))
//                .onErrorResume(e -> Mono.just(Map.of("status", "ERROR", "message", e.getMessage())));
//    }
//}

package com.project.hems.api_gateway_hems.controller;

import com.project.hems.api_gateway_hems.service.impl.Auth0ManagementServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthFlowController {

    private final Auth0ManagementServiceImpl mgmt;

    // Simple email validation regex
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Helper method to create response map with consistent typing
    private Map<String, Object> response(String status, String message) {
        return Map.<String, Object>of("status", status, "message", message);
    }

    @PostMapping("/check-email-and-handle")
    public Mono<Map<String, Object>> check(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        // Input validation
        if (email == null || email.isBlank()) {
            log.warn("[v0] check-email-and-handle called with null/blank email");
            return Mono.just(response("ERROR", "Email cannot be empty"));
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            log.warn("[v0] check-email-and-handle called with invalid email format: {}", email);
            return Mono.just(response("ERROR", "Invalid email format"));
        }

        log.info("[v0] Checking email and handling social flow: {}", email);

        return mgmt.isSocialOnlyUser(email)
                .flatMap(isSocialOnly -> {
                    if (!isSocialOnly) {
                        log.debug("[v0] User {} has DB account, not social-only", email);
                        return Mono.just(response("OK", "Proceed with normal DB login"));
                    }

                    log.info("[v0] User {} is social-only, triggering password setup flow", email);

                    // Full C3 flow: create DB identity + link + send email
                    return mgmt.sendPasswordSetupForSocialUser(email)
                            .then(Mono.just(response(
                                    "PASSWORD_SETUP_EMAIL_SENT",
                                    "Password setup email sent. Check inbox/spam."
                            )))
                            .doOnError(e -> log.error("[v0] Password setup flow failed for {}", email, e));
                })
                .onErrorResume(e -> {
                    log.error("[v0] Error in check-email-and-handle: {}", e.getMessage());
                    return Mono.just(response("ERROR", "An error occurred. Please try again later."));
                });
    }

    @PostMapping("/debug-c3")
    @PreAuthorize("hasRole('ADMIN')")  // SECURITY FIX: Only admins can trigger debug
    public Mono<Map<String, String>> debug(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        // Input validation
        if (email == null || email.isBlank()) {
            log.warn("[v0] debug-c3 called with null/blank email");
            return Mono.just(Map.of(
                    "status", "ERROR",
                    "message", "Email cannot be empty"
            ));
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            log.warn("[v0] debug-c3 called with invalid email format: {}", email);
            return Mono.just(Map.of(
                    "status", "ERROR",
                    "message", "Invalid email format"
            ));
        }

        log.info("[v0] DEBUG: Triggering C3 flow for {}", email);

        return mgmt.sendPasswordSetupForSocialUser(email)
                .thenReturn(Map.of(
                        "status", "OK",
                        "message", "C3 flow triggered for " + email
                ))
                .onErrorResume(e -> {
                    log.error("[v0] DEBUG: C3 flow failed for {}: {}", email, e.getMessage());
                    return Mono.just(Map.of(
                            "status", "ERROR",
                            "message", "C3 flow failed: " + e.getMessage()
                    ));
                });

}}