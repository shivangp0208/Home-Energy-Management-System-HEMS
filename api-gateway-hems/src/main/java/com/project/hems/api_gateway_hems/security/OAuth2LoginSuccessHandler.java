//package com.project.hems.api_gateway_hems.security;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseCookie;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.web.server.WebFilterExchange;
//import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
//import reactor.core.publisher.Mono;
//
//import java.time.Duration;
//
//@RequiredArgsConstructor
//public class OAuth2LoginSuccessHandler implements ServerAuthenticationSuccessHandler {
//
//    private final ReactiveOAuth2AuthorizedClientService clientService;
//
//    @Override
//    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange,
//                                              Authentication authentication) {
//
//        OAuth2AuthenticationToken oauth =
//                (OAuth2AuthenticationToken) authentication;
//
//        return clientService
//                .loadAuthorizedClient(
//                        oauth.getAuthorizedClientRegistrationId(),
//                        oauth.getName()
//                )
//                .switchIfEmpty(Mono.error(new IllegalStateException("Authorized client not found")))
//                .flatMap(client -> {
//
//                    String token = client.getAccessToken().getTokenValue();
//
//                    ResponseCookie cookie = ResponseCookie.from("jwt", token)
//                            .httpOnly(true)
//                            .secure(true)
//                            .sameSite("Strict")
//                            .path("/")
//                            .maxAge(Duration.ofHours(24))
//                            .build();
//
//                    webFilterExchange.getExchange()
//                            .getResponse()
//                            .addCookie(cookie);
//
//                    return webFilterExchange.getChain()
//                            .filter(webFilterExchange.getExchange());
//                });
//    }
//}