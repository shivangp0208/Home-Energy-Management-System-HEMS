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

    //@Value("${auth0.domain}")       // hostname only (e.g. dev-xxxx.us.auth0.com)
    private String auth0Domain="dev-0x5bg1uy1egiz4y0.us.auth0.com";

    @Value("${auth0.client-id}")    // regular Application client_id (NOT mgmt client)
    private String clientId;

    public PasswordSetupController(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    /**
     * Sends password reset/setup email for DB connection users only.
     * NOTE: This will NOT work for Google/Github-only users unless they already have a DB identity.
     */
    @PostMapping("/send-password-setup-email")
    public Mono<String> send(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return Mono.error(new IllegalArgumentException("email is required"));
        }

        String url = "https://" + auth0Domain + "/dbconnections/change_password";

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
                .thenReturn("Password reset email requested for " + email)
                .doOnError(err -> log.error("Password setup email flow failed", err));
    }
}
