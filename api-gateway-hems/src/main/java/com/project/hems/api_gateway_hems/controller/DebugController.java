package com.project.hems.api_gateway_hems.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class DebugController {
    private final ReactiveOAuth2AuthorizedClientService service;

    public DebugController(ReactiveOAuth2AuthorizedClientService service) {
        this.service = service;
    }

    @GetMapping("/debug/token")
    public Mono<String> token(@AuthenticationPrincipal OidcUser user) {

        return service
                .loadAuthorizedClient("auth0", user.getName())
                .map(client -> client.getAccessToken().getTokenValue());
    }

}
