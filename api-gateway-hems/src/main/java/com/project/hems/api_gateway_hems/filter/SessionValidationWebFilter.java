////package com.project.hems.api_gateway_hems.filter;
////
////import lombok.RequiredArgsConstructor;
////import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
////import org.springframework.http.HttpStatus;
////import org.springframework.security.core.Authentication;
////import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
////import org.springframework.stereotype.Component;
////import org.springframework.web.server.ServerWebExchange;
////import org.springframework.web.server.WebFilter;
////import org.springframework.web.server.WebFilterChain;
////import reactor.core.publisher.Mono;
////
////@Component
////@RequiredArgsConstructor
////public class SessionValidationWebFilter implements WebFilter {
////
////    private final ReactiveStringRedisTemplate redis;
////
////
////    @Override
////    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
////
////        String path = exchange.getRequest().getURI().getPath();
////
////        if ("/session/activate".equals(path)) {
////            return chain.filter(exchange);
////        }
////        Mono<Authentication> auth = exchange.getPrincipal()
////                .cast(Authentication.class);
////
////
////        return auth.flatMap(a -> {
////            if (a instanceof JwtAuthenticationToken jwtAuth) {
////                var jwt = jwtAuth.getToken();
////                String sub = jwt.getSubject();
////                String sid = jwt.getClaimAsString("https://hems.example.com/sid");
////
////
////                if (sub != null && sid != null) {
////                    String key = "session:" + sub;
////                    Object currentSidObj = redis.opsForHash().get(key, "sid");
////                    String currentSid = currentSidObj == null ? null : currentSidObj.toString();
////
////                    if (currentSid == null || !sid.equals(currentSid)) {
////                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
////                        return exchange.getResponse().setComplete();
////                    }
////                }
////            }
////            return chain.filter(exchange);
////        }).switchIfEmpty(chain.filter(exchange)); // if no principal then agad vadhi jaisu
////    }
////}
//
//
//package com.project.hems.api_gateway_hems.filter;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ResponseStatusException;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//@Component
//@RequiredArgsConstructor
//public class SessionValidationWebFilter implements WebFilter {
//
//    private final ReactiveStringRedisTemplate redis;
//
//    private static String sessionsKey(String sub) {
//        return "sessions:" + sub;
//    }
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        String path = exchange.getRequest().getURI().getPath();
//
//        if ("/session/activate".equals(path) || "/session/logout".equals(path)) {
//            return chain.filter(exchange);
//        }
//
//        return exchange.getPrincipal()
//                .cast(Authentication.class)
//                .flatMap(authentication -> {
//                    if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
//                        return chain.filter(exchange);
//                    }
//
//                    String sub = jwtAuth.getToken().getSubject();
//                    String sid = jwtAuth.getToken().getClaimAsString("https://hems.example.com/sid");
//
//                    if (sub == null || sid == null) {
//                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                        return exchange.getResponse().setComplete();
//                    }
//
//                    return redis.opsForHash()
//                            .hasKey(sessionsKey(sub), sid)
//                            .flatMap(exists -> {
//                                if (!exists) {
//                                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                                    return exchange.getResponse().setComplete();
//                                }
//                                return chain.filter(exchange);
//                            });
//                })
//                .switchIfEmpty(chain.filter(exchange));
//    }
//}


package com.project.hems.api_gateway_hems.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * SessionValidationWebFilter - Validates JWT session in Redis
 *
 * Flow:
 * 1. Check if request is for /session/activate or /session/logout -> bypass
 * 2. Extract JWT from principal
 * 3. Get sub (user ID) and sid (session ID) from JWT
 * 4. Query Redis for session:sub hash key
 * 5. Check if sid exists in hash
 * 6. If valid -> continue, if invalid -> throw ResponseStatusException
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SessionValidationWebFilter implements WebFilter {

    private final ReactiveStringRedisTemplate redis;

    /**
     * Generate Redis key for session storage
     * Format: sessions:{userId}
     */
    private static String getSessionKey(String userId) {
        return "sessions:" + userId;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().toString();

        log.debug("[v0] SessionValidationWebFilter - {} {}", method, path);

        // Skip validation for public endpoints
        if (isPublicPath(path)) {
            log.debug("[v0] Public path, skipping session validation: {}", path);
            return chain.filter(exchange);
        }

        // Extract authentication from principal
        return exchange.getPrincipal()
                .cast(Authentication.class)
                .flatMap(this::validateSession)
                .then(chain.filter(exchange))
                .onErrorResume(this::handleError)
                .switchIfEmpty(chain.filter(exchange));
    }

    /**
     * Validate JWT session against Redis
     */
    private Mono<Void> validateSession(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            log.warn("[v0] Authentication is not JWT: {}", authentication.getClass().getSimpleName());
            return Mono.error(
                    new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication type")
            );
        }

        var jwt = jwtAuth.getToken();
        String userId = jwt.getSubject();
        String sessionId = jwt.getClaimAsString("https://hems.example.com/sid");

        // Validate claims exist
        if (userId == null || userId.isBlank()) {
            log.warn("[v0] JWT missing subject (sub) claim");
            return Mono.error(
                    new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing user ID in token")
            );
        }

        if (sessionId == null || sessionId.isBlank()) {
            log.warn("[v0] JWT missing session ID (sid) claim for user: {}", userId);
            return Mono.error(
                    new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing session ID in token")
            );
        }

        // Query Redis for valid session
        String sessionKey = getSessionKey(userId);
        log.debug("[v0] Checking session validity - key: {}, sessionId: {}", sessionKey, sessionId);

        return redis.opsForHash()
                .hasKey(sessionKey, sessionId)
                .flatMap(exists -> {
                    if (exists) {
                        log.debug("[v0] Session valid for user: {}", userId);
                        return Mono.empty();
                    } else {
                        log.warn("[v0] Session not found in Redis - user: {}, sessionId: {}", userId, sessionId);
                        return Mono.error(
                                new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Session invalid or expired"
                                )
                        );
                    }
                });
    }

    /**
     * Handle any errors from validation
     */
    private Mono<Void> handleError(Throwable ex) {
        if (ex instanceof ResponseStatusException rse) {
            log.warn("[v0] Session validation failed: {}", rse.getReason());
            return Mono.error(rse);
        }

        log.error("[v0] Unexpected error in session validation", ex);
        return Mono.error(
                new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Session validation failed"
                )
        );
    }

    /**
     * Check if path is public and should skip session validation
     */
    private boolean isPublicPath(String path) {
        return path.equals("/session/activate")
                || path.equals("/session/logout")
                || path.equals("/auth/login")
                || path.equals("/auth/signup")
                || path.equals("/auth/refresh")
                || path.equals("/health")
                || path.equals("/actuator/health")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }
}