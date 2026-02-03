package com.project.hems.api_gateway_hems.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class Auth0ManagementService {
    private static final Logger log = LoggerFactory.getLogger(Auth0ManagementService.class);

    private final WebClient webClient;

    @Value("${auth0.domain}")
    private String auth0Domain;

    @Value("${auth0-mgmt.client-id}")
    private String mgmtClientId;

    @Value("${auth0-mgmt.client-secret}")
    private String mgmtClientSecret;

    private Mono<String> cachedToken;

    public Auth0ManagementService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    private Mono<String> getMgmtToken() {
        if (cachedToken != null) return cachedToken;

        String tokenUrl = "https://" + auth0Domain + "/oauth/token";

        cachedToken = webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "client_id", mgmtClientId,
                        "client_secret", mgmtClientSecret,
                        "audience", "https://" + auth0Domain + "/api/v2/",
                        "grant_type", "client_credentials"
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(m -> (String) m.get("access_token"))
                .doOnNext(t -> log.info("MGMT token fetched OK"))
                // cache token for ~20 minutes (simple cache)
                .cache(Duration.ofMinutes(20));

        return cachedToken;
    }

    /**
     * If no DB user exists for this email, create one in Username-Password-Authentication.
     * Note: Auth0 may still allow duplicate emails across connections, but we create only if missing.
     */
    public Mono<Void> ensureDbUserExists(String email) {
        return getMgmtToken()
                .flatMap(token -> searchUsersByEmail(token, email))
                .flatMap(found -> {
                    boolean hasDb = found.stream().anyMatch(u ->
                            "auth0".equals(u.get("provider")) // we fill provider in mapping below
                    );
                    if (hasDb) {
                        log.info("DB user already exists for {}", email);
                        return Mono.empty();
                    }
                    log.info("No DB user found for {}. Creating DB user...", email);
                    return getMgmtToken().flatMap(token -> createDbUser(token, email));
                });
    }

    private Mono<List<Map<String, Object>>> searchUsersByEmail(String token, String email) {
        String url = "https://" + auth0Domain + "/api/v2/users-by-email?email=" + email;

        return webClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(List.class)
                .map(list -> {
                    // enrich each user with "provider" = first identity provider
                    for (Object obj : list) {
                        Map<String, Object> u = (Map<String, Object>) obj;
                        List<Map<String, Object>> identities = (List<Map<String, Object>>) u.get("identities");
                        if (identities != null && !identities.isEmpty()) {
                            u.put("provider", identities.get(0).get("provider"));
                        }
                    }
                    return (List<Map<String, Object>>) (List<?>) list;
                })
                .doOnNext(users -> log.info("Users-by-email returned {} record(s) for {}", users.size(), email));
    }

    private Mono<Void> createDbUser(String token, String email) {
        String url = "https://" + auth0Domain + "/api/v2/users";

        // Random temp password (user will reset anyway)
        String tempPassword = "Temp@" + System.currentTimeMillis();

        Map<String, Object> payload = Map.of(
                "connection", "Username-Password-Authentication",
                "email", email,
                "password", tempPassword,
                "email_verified", true
        );

        return webClient.post()
                .uri(url)
                .headers(h -> h.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(resp -> log.info("DB user created for {}. Response={}", email, resp))
                .then();
    }
}
