package com.project.hems.api_gateway_hems.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthFlowController {

    private final Auth0ManagementService mgmt;

    public AuthFlowController(Auth0ManagementService mgmt) {
        this.mgmt = mgmt;
    }

    @PostMapping("/check-email-and-handle")
    public Mono<Map<String, Object>> check(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        return mgmt.isSocialOnlyUser(email)
                .flatMap(isSocialOnly -> {
                    if (!isSocialOnly) {
                        return Mono.just(Map.of(
                                "status", "OK",
                                "message", "Proceed with normal DB login"
                        ));
                    }

                    // ✅ FIX: this must be the full C3 flow (create DB identity + link + email)
                    return mgmt.sendPasswordSetupForSocialUser(email)
                            .thenReturn(Map.of(
                                    "status", "PASSWORD_SETUP_EMAIL_SENT",
                                    "message", "This email is registered with Google/GitHub. We sent a password setup email. Check inbox/spam."
                            ));
                });
    }

    // optional debug endpoint if you want
    @PostMapping("/debug-c3")
    public Mono<Map<String, String>> debug(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        return mgmt.sendPasswordSetupForSocialUser(email)
                .thenReturn(Map.of("status", "OK", "message", "[C3] triggered for " + email))
                .onErrorResume(e -> Mono.just(Map.of("status", "ERROR", "message", e.getMessage())));
    }
}
