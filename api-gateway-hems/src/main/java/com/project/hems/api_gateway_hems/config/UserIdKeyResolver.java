package com.project.hems.api_gateway_hems.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Component("userIdKeyResolver")
public class UserIdKeyResolver implements KeyResolver {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return getClientIpAsFallback(exchange);
        }

        try {
            String token = authHeader.substring(7);

            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return getClientIpAsFallback(exchange);
            }

            String payload = parts[1];
            payload += "=".repeat((4 - payload.length() % 4) % 4);

            // ✅ FIX: Use URL decoder
            byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
            String decodedPayload = new String(decodedBytes);

            JsonNode jsonNode = objectMapper.readTree(decodedPayload);

            // ✅ FIX: Null-safe extraction
            JsonNode subNode = jsonNode.get("sub");

            if (subNode != null && !subNode.asText().isEmpty()) {
                String userId = subNode.asText();
                System.out.println("[RATE-LIMIT] Using userId: " + userId);
                return Mono.just(userId);
            }

        } catch (Exception e) {
            System.out.println("[RATE-LIMIT] JWT parse error: " + e.getMessage());
        }

        return getClientIpAsFallback(exchange);
    }

    private Mono<String> getClientIpAsFallback(ServerWebExchange exchange) {

        String ip = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Forwarded-For");

        if (ip == null && exchange.getRequest().getRemoteAddress() != null) {
            ip = exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress()
                    .getHostAddress();
        }

        if (ip == null) {
            ip = "unknown";
        }

        System.out.println("[RATE-LIMIT] Using IP fallback: " + ip);
        return Mono.just(ip);
    }
}