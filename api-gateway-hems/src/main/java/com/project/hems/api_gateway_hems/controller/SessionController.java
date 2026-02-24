//package com.project.hems.api_gateway_hems.controller;
//
//import com.project.hems.api_gateway_hems.dto.ActivateSessionRequest;
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.time.Duration;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//@RestController
//@RequestMapping("/session")
//@RequiredArgsConstructor
//public class SessionController {
//
//    private final StringRedisTemplate redis;
//
//        @PostMapping("/activate")
//        public Mono<ResponseEntity<?>> activate(@RequestBody ActivateSessionRequest request,
//                                                @RequestHeader(value = "User-Agent", defaultValue = "unknown") String userAgent,
//                                               // @RequestHeader(value = "X-Forwarded-For", defaultValue = "unknown") String ip,
//                                                ServerWebExchange exchange,
//                                                @AuthenticationPrincipal Jwt jwt) {
//
//            String sub = jwt.getSubject();
//            String key = "session:" + sub;
//            String ip = extractClientIp(exchange);
//            String ua = exchange.getRequest()
//                    .getHeaders()
//                    .getFirst("User-Agent");
//
//            String existingDeviceId = (String) redis.opsForHash().get(key, "deviceId");
//
//            if (existingDeviceId != null && !existingDeviceId.equals(request.getDeviceId())) {
//
//                log.warn("Login rejected. User {} already active on another device {}", sub, existingDeviceId);
//
//                return Mono.just(
//                        ResponseEntity.status(HttpStatus.CONFLICT)
//                                .body("User already logged in from another device")
//                );
//            }
//             String hostIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
//
//            Map<String, String> sessionData = new HashMap<>();
//            sessionData.put("deviceId", request.getDeviceId());
//            sessionData.put("ip", hostIp);
//            sessionData.put("ua", ua);
//            sessionData.put("lastLoginAt", String.valueOf(Instant.now().toEpochMilli()));
//
//            redis.opsForHash().putAll(key, sessionData);
//
//            log.info("Session activated successfully for user {}", sub);
//
//            return Mono.just(ResponseEntity.ok("Session activated successfully"));
//        }
//    private String extractClientIp(ServerWebExchange exchange) {
//
//        String xff = exchange.getRequest()
//                .getHeaders()
//                .getFirst("X-Forwarded-For");
//
//        if (xff != null && !xff.isBlank()) {
//            return xff.split(",")[0].trim();
//        }
//
//        var remoteAddress = exchange.getRequest().getRemoteAddress();
//
//        if (remoteAddress == null ||
//                remoteAddress.getAddress() == null) {
//            return "unknown";
//        }
//
//        return remoteAddress.getAddress().getHostAddress();
//    }
//
//        @PostMapping("/logout")
//        public Mono<ResponseEntity<?>> logout(@AuthenticationPrincipal Jwt jwt) {
//
//            String sub = jwt.getSubject();
//            String key = "session:" + sub;
//
//            redis.delete(key);
//
//            log.info(" Session cleared for user {}", sub);
//
//            return Mono.just(ResponseEntity.ok("Logged out successfully"));
//        }
//
//
//    }
//
//
//


//-----------------------------------------------------------------
package com.project.hems.api_gateway_hems.controller;

import com.project.hems.api_gateway_hems.dto.ActivateSessionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionController {

    private final ReactiveStringRedisTemplate redis;

    private static String devicesKey(String sub) { return "devices:" + sub; }
    private static String sessionsKey(String sub) { return "sessions:" + sub; }
    private static String metaKey(String sub, String sid) { return "sessionmeta:" + sub + ":" + sid; }

    @PostMapping("/activate")
    public Mono<ResponseEntity<?>> activate(
            @RequestBody ActivateSessionRequest req,
            ServerWebExchange exchange,
            @AuthenticationPrincipal Jwt jwt
    ) {
        exchange.getRequest().getHeaders().forEach((key,value)->{
            log.info("header {} = {}", key, value);
        });
        if (jwt == null) return Mono.just(ResponseEntity.status(401).build());

        String sub = jwt.getSubject();
        String sid = jwt.getClaimAsString("https://hems.example.com/sid");
        String fingerprint = req.getFingerprint();

        if (sid == null || sid.isBlank()) {
            return Mono.just(ResponseEntity.badRequest().body("Missing sid claim in JWT"));
        }
        if (fingerprint == null || fingerprint.isBlank()) {
            return Mono.just(ResponseEntity.badRequest().body("Missing fingerprint in request"));
        }

        String ip = extractClientIp(exchange);
        String ua = exchange.getRequest().getHeaders().getFirst("User-Agent");
        if (ua == null) ua = "unknown";

        String dKey = devicesKey(sub);
        String sKey = sessionsKey(sub);

        //here we check is fingerprint is already trusted/known
        Mono<Boolean> fingerprintKnown =
                redis.opsForHash().hasKey(dKey, fingerprint);

        String finalUa = ua;
        return fingerprintKnown.flatMap(known -> {
            Mono<Void> trustDeviceStep;

            if (!known) {
                //secyrity email trigger karvo
                log.warn("New device detected for sub={} fingerprint={}", sub, fingerprint);

                trustDeviceStep = redis.opsForHash()
                        .put(dKey, fingerprint, String.valueOf(Instant.now().toEpochMilli()))
                        .then(redis.expire(dKey, Duration.ofDays(365)))
                        .then();
            } else {
                trustDeviceStep = Mono.empty();
            }

            // save active session sid -> fingerprint
            Mono<Void> saveSessionPointer =
                    redis.opsForHash().put(sKey, sid, fingerprint)
                            .then(redis.expire(sKey, Duration.ofHours(2)))
                            .then();

            // save session metadata
            Mono<Void> saveMeta =
                    redis.opsForHash().putAll(metaKey(sub, sid), Map.of(
                                    "sid", sid,
                                    "fingerprint", fingerprint,
                                    "ip", ip,
                                    "ua", finalUa,
                                    "lastLoginAt", String.valueOf(Instant.now().toEpochMilli())
                            ))
                            .then(redis.expire(metaKey(sub, sid), Duration.ofHours(2)))
                            .then();

            // SINGLE SESSION POLICY (force logout old sessions)
            // If you want ONLY 1 session per user, uncomment this block:
            /*
            Mono<Void> enforceSingleSession =
                    redis.opsForHash().keys(sKey)
                        .flatMap(oldSid -> {
                            if (sid.equals(oldSid)) return Mono.empty();
                            return redis.opsForHash().remove(sKey, oldSid)
                                    .then(redis.delete(metaKey(sub, oldSid)))
                                    .then();
                        })
                        .then();
            */

            return trustDeviceStep
                    .then(saveSessionPointer)
                    .then(saveMeta)
                    // .then(enforceSingleSession) // enable if single session
                    .thenReturn(ResponseEntity.ok(known ? "Session activated" : "New device detected + session activated"));
        });
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<?>> logout(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) return Mono.just(ResponseEntity.status(401).build());

        String sub = jwt.getSubject();
        String sid = jwt.getClaimAsString("https://hems.example.com/sid");
        if (sid == null) return Mono.just(ResponseEntity.status(HttpStatus.OK).body("Logged out"));

        String sKey = sessionsKey(sub);

        return redis.opsForHash().remove(sKey, sid)
                .then(redis.delete(metaKey(sub, sid)))
                .thenReturn(ResponseEntity.ok("Logged out"));
    }

    private String extractClientIp(ServerWebExchange exchange) {
        //log.info("exchange header name {} ",exchange.getRequest().getHeaders("X-Forwarded-For"));

        log.info("goes into extract method");
        String xff = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");

        log.info("xff is {} ",xff);

        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();

        var remote = exchange.getRequest().getRemoteAddress();
        if (remote == null || remote.getAddress() == null) return "unknown";

        String host = remote.getAddress().getHostAddress();
        return (host == null || host.isBlank()) ? "unknown" : host;
    }
}
