package com.project.hems.api_gateway_hems.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class Auth0ManagementService {

    private static final Logger log = LoggerFactory.getLogger(Auth0ManagementService.class);

    private final WebClient webClient;

    @Value("${auth0.domain}") // dev-xxxx.us.auth0.com (NO https://)
    private String auth0Domain;

    @Value("${auth0-mgmt.client-id}")
    private String mgmtClientId;

    @Value("${auth0-mgmt.client-secret}")
    private String mgmtClientSecret;

    @Value("${auth0.client-id}") // regular app client_id for change_password endpoint
    private String clientId;

    private Mono<String> cachedToken;

    public Auth0ManagementService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    // ===================== HELPERS =====================

    private String normalizeEmail(String raw) {
        if (raw == null) return null;
        // If UI sends already-encoded (contains %40), decode it
        String decoded = URLDecoder.decode(raw, StandardCharsets.UTF_8);
        return decoded.trim();
    }

    private String encodeQueryParam(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String stripProviderPrefix(String userId) {
        if (userId == null) return null;
        int idx = userId.indexOf('|');
        return idx >= 0 ? userId.substring(idx + 1) : userId;
    }

    // private String encodeUserId(String userId) {
    //     // minimal encoding for path param
    //     return userId.replace("|", "%7C");
    // }

    // ===================== MGMT TOKEN =====================
    
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
                .cache(Duration.ofMinutes(20));

        return cachedToken;
    }

    // ===================== USERS SEARCH =====================

    public Mono<List<Map<String, Object>>> usersByEmail(String emailRaw) {
        String email = normalizeEmail(emailRaw);
        return getMgmtToken().flatMap(token -> usersByEmail(token, email));
    }

   private Mono<List<Map<String, Object>>> usersByEmail(String token, String email) {

    log.info("[C3] usersByEmail calling for email={}", email);

    return webClient.get()
            .uri(uriBuilder -> uriBuilder
                    .scheme("https")
                    .host(auth0Domain)
                    .path("/api/v2/users-by-email")
                    .queryParam("email", email)   // ✅ LET WebClient encode
                    .build()
            )
            .headers(h -> h.setBearerAuth(token))
            .retrieve()
            .bodyToMono(List.class)
            .map(list -> (List<Map<String, Object>>) (List<?>) list)
            .doOnNext(users ->
                    log.info("[C3] users-by-email returned {} record(s) for {}", users.size(), email)
            );
}

    public Mono<Boolean> isSocialOnlyUser(String emailRaw) {
        String email = normalizeEmail(emailRaw);

        return usersByEmail(email)
                .map(users -> {
                    if (users == null || users.isEmpty()) return false;

                    boolean hasSocial = false;
                    boolean hasDb = false;

                    for (Map<String, Object> u : users) {
                        List<Map<String, Object>> identities = (List<Map<String, Object>>) u.get("identities");
                        if (identities == null) continue;

                        for (Map<String, Object> id : identities) {
                            String provider = (String) id.get("provider");
                            if ("auth0".equals(provider)) hasDb = true;
                            else hasSocial = true;
                        }
                    }

                    return hasSocial && !hasDb;
                });
    }

    // ===================== CHANGE PASSWORD EMAIL =====================

    public Mono<String> sendChangePasswordEmail(String emailRaw) {
        String email = normalizeEmail(emailRaw);
        String url = "https://" + auth0Domain + "/dbconnections/change_password";

        log.info("[C3] Sending change_password email for {}", email);

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
                .doOnNext(resp -> log.info("[C3] change_password response: {}", resp));
    }

    // ===================== MAIN FLOW (SOCIAL -> PASSWORD SETUP) =====================

    public Mono<Void> sendPasswordSetupForSocialUser(String emailRaw) {
        String email = normalizeEmail(emailRaw);

        return getMgmtToken()
                .flatMap(token -> usersByEmail(token, email))
                .flatMap(users -> {

                    if (users == null || users.isEmpty()) {
                        log.info("[C3] No user found for {}, nothing to do", email);
                        return Mono.empty();
                    }

                    // Prefer a social user as primary
                    Map<String, Object> primary = users.stream()
                            .filter(this::hasAnyNonAuth0Identity)
                            .findFirst()
                            .orElse(users.get(0));

                    if (hasProvider(primary, "auth0")) {
                        log.info("[C3] User already has DB identity. Sending reset email only.");
                        return sendChangePasswordEmail(email).then();
                    }

                    String primaryUserId = (String) primary.get("user_id");
                    if (primaryUserId == null || primaryUserId.isBlank()) {
                        return Mono.error(new IllegalStateException("Primary user_id missing"));
                    }

                    log.info("[C3] Primary social user_id={}. Creating DB identity + linking…", primaryUserId);

                    return createDbUser(email)
                            .flatMap(dbUserId -> linkDbIdentityToPrimary(primaryUserId, dbUserId))
                            .then(sendChangePasswordEmail(email))
                            .then();
                })
                .doOnSuccess(v -> log.info("[C3] Completed password setup flow for {}", email))
                .doOnError(e -> log.error("[C3] sendPasswordSetupForSocialUser failed for {}", email, e));
    }

    private boolean hasAnyNonAuth0Identity(Map<String, Object> user) {
        List<Map<String, Object>> identities = (List<Map<String, Object>>) user.get("identities");
        if (identities == null) return false;
        return identities.stream().anyMatch(i -> {
            String provider = (String) i.get("provider");
            return provider != null && !"auth0".equals(provider);
        });
    }

    private boolean hasProvider(Map<String, Object> user, String provider) {
        List<Map<String, Object>> identities = (List<Map<String, Object>>) user.get("identities");
        if (identities == null) return false;
        return identities.stream().anyMatch(i -> provider.equals(i.get("provider")));
    }

    // ===================== CREATE DB USER =====================

    private Mono<String> createDbUser(String email) {
        return getMgmtToken().flatMap(token -> {
            String url = "https://" + auth0Domain + "/api/v2/users";
            String tempPassword = "Temp@" + System.currentTimeMillis();

            Map<String, Object> payload = Map.of(
                    "connection", "Username-Password-Authentication",
                    "email", email,
                    "password", tempPassword,
                    "email_verified", false
            );

            log.info("[C3] Creating DB user for {}", email);

            return webClient.post()
                    .uri(url)
                    .headers(h -> h.setBearerAuth(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(resp -> (String) resp.get("user_id"))
                    .doOnNext(userId -> log.info("[C3] DB user created for {} -> {}", email, userId));
        });
    }

    // ===================== LINK IDENTITIES =====================

    private Mono<Void> linkDbIdentityToPrimary(String primaryUserId, String dbUserId) {
    return getMgmtToken().flatMap(token -> {

        String secondaryIdOnly = stripProviderPrefix(dbUserId); // auth0|abc -> abc

        Map<String, Object> payload = Map.of(
                "provider", "auth0",
                "user_id", secondaryIdOnly
        );

        log.info("[C3] Linking DB identity. primaryUserId={}, dbUserId={}", primaryUserId, dbUserId);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(auth0Domain)
                        .pathSegment("api", "v2", "users", primaryUserId, "identities")
                        .build()
                )
                .headers(h -> h.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()

                // ✅ ADD THIS to see real Auth0 error body
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException(
                                        "[C3] link identities failed " + resp.statusCode() + " body=" + body
                                )))
                )

                .bodyToMono(String.class)
                .doOnNext(resp -> log.info("[C3] link identities success resp={}", resp))
                .then();
    });
}
}
