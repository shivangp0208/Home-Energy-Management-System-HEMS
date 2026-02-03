package com.project.hems.api_gateway_hems.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class PasswordSetupController {

    private static final Logger log = LoggerFactory.getLogger(PasswordSetupController.class);

    private final WebClient webClient;
    private final Auth0ManagementService mgmt;

    @Value("${auth0.domain}")       // hostname only
    private String auth0Domain;

    @Value("${auth0.client-id}")    // regular app client_id (works for change_password)
    private String clientId;

    public PasswordSetupController(WebClient.Builder builder, Auth0ManagementService mgmt) {
        this.webClient = builder.build();
        this.mgmt = mgmt;
    }

    @PostMapping("/send-password-setup-email")
    public Mono<String> send(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return Mono.error(new IllegalArgumentException("email is required"));
        }

        String url = "https://" + auth0Domain + "/dbconnections/change_password";

        return mgmt.ensureDbUserExists(email)
                .then(Mono.defer(() -> {
                    log.info("Calling Auth0 change_password. url={}, email={}", url, email);

                    return webClient.post()
                            .uri(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of(
                                    "client_id", clientId,
                                    "email", email,
                                    "connection", "Username-Password-Authentication"
                            ))
                            .retrieve()
                            .bodyToMono(String.class)
                            .doOnNext(resp -> log.info("Auth0 change_password response body: {}", resp))
                            .map(x -> "Password reset email requested for " + email);
                }))
                .doOnError(err -> log.error("Password setup email flow failed", err));
    }
}
