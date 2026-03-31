//package com.project.hems.api_gateway_hems.filter;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//import org.springframework.stereotype.Component;
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
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//
//        String path = exchange.getRequest().getURI().getPath();
//
//        if ("/session/activate".equals(path)) {
//            return chain.filter(exchange);
//        }
//        Mono<Authentication> auth = exchange.getPrincipal()
//                .cast(Authentication.class);
//
//
//        return auth.flatMap(a -> {
//            if (a instanceof JwtAuthenticationToken jwtAuth) {
//                var jwt = jwtAuth.getToken();
//                String sub = jwt.getSubject();
//                String sid = jwt.getClaimAsString("https://hems.example.com/sid");
//
//
//                if (sub != null && sid != null) {
//                    String key = "session:" + sub;
//                    Object currentSidObj = redis.opsForHash().get(key, "sid");
//                    String currentSid = currentSidObj == null ? null : currentSidObj.toString();
//
//                    if (currentSid == null || !sid.equals(currentSid)) {
//                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                        return exchange.getResponse().setComplete();
//                    }
//                }
//            }
//            return chain.filter(exchange);
//        }).switchIfEmpty(chain.filter(exchange)); // if no principal then agad vadhi jaisu
//    }
//}


package com.project.hems.api_gateway_hems.filter;

import lombok.RequiredArgsConstructor;
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

@Component
@RequiredArgsConstructor
public class SessionValidationWebFilter implements WebFilter {

    private final ReactiveStringRedisTemplate redis;

    private static String sessionsKey(String sub) {
        return "sessions:" + sub;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if ("/session/activate".equals(path) || "/session/logout".equals(path)) {
            return chain.filter(exchange);
        }

        return exchange.getPrincipal()
                .cast(Authentication.class)
                .flatMap(authentication -> {
                    if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
                        return chain.filter(exchange);
                    }

                    String sub = jwtAuth.getToken().getSubject();
                    String sid = jwtAuth.getToken().getClaimAsString("https://hems.example.com/sid");

                    if (sub == null || sid == null) {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }

                    return redis.opsForHash()
                            .hasKey(sessionsKey(sub), sid)
                            .flatMap(exists -> {
                                if (!exists) {
                                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                    return exchange.getResponse().setComplete();
                                }
                                return chain.filter(exchange);
                            });
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}