package com.project.hems.api_gateway_hems.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RateLimitLogFilter implements GlobalFilter {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final KeyResolver userIdKeyResolver;

    public RateLimitLogFilter(ReactiveRedisTemplate<String, String> redisTemplate,
                              KeyResolver userIdKeyResolver) {
        this.redisTemplate = redisTemplate;
        this.userIdKeyResolver = userIdKeyResolver;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return userIdKeyResolver.resolve(exchange)
                .flatMap(id -> {

                    // ✅ Correct Redis key used by Spring Cloud Gateway
                    String tokenKey = "request_rate_limiter." + id + ".tokens";

                    return redisTemplate.opsForValue().get(tokenKey)
                            .doOnNext(tokens -> {
                                System.out.println("[RATE-LIMIT] key=" + id +
                                        " path=" + exchange.getRequest().getPath() +
                                        " remainingTokens=" + tokens);
                            })
                            .then(chain.filter(exchange));
                });
    }
}