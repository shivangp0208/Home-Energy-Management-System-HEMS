package com.project.hems.api_gateway_hems.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;

@Slf4j
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ReactiveClientRegistrationRepository clientRegistrationRepository
    ) {

        ServerAuthenticationSuccessHandler successHandler =
                (webFilterExchange, authentication) -> {

                    OidcUser user = (OidcUser) authentication.getPrincipal();
                    log.debug("successHandler: LOGIN SUCCESS: " + user.getEmail());

                    webFilterExchange.getExchange().getResponse()
                            .setStatusCode(HttpStatus.FOUND);
                    webFilterExchange.getExchange().getResponse()
                            .getHeaders()
                            .setLocation(URI.create("/auth/create-user"));

                    return webFilterExchange.getExchange().getResponse().setComplete();
                };

        ServerAuthenticationFailureHandler failureHandler =
                (webFilterExchange, exception) -> {

                    log.error("failureHandler: LOGIN FAILED: " + exception.getMessage());

                    webFilterExchange.getExchange().getResponse()
                            .setStatusCode(HttpStatus.FOUND);
                    webFilterExchange.getExchange().getResponse()
                            .getHeaders()
                            .setLocation(URI.create("/error"));

                    return webFilterExchange.getExchange().getResponse().setComplete();
                };

        ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver =
                authorizationRequestResolver(clientRegistrationRepository);

        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/login", "/debug/token").permitAll()
                        .anyExchange().authenticated()
                )

                .oauth2Login(oauth2 -> oauth2
                        .authorizationRequestResolver(authorizationRequestResolver)
                        .authenticationSuccessHandler(successHandler)
                        .authenticationFailureHandler(failureHandler)
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(Customizer.withDefaults())
                );

        return http.build();
    }


    @Bean
    public ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ReactiveClientRegistrationRepository clientRegistrationRepository
    ) {

        DefaultServerOAuth2AuthorizationRequestResolver resolver =
                new DefaultServerOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository
                );

        resolver.setAuthorizationRequestCustomizer(customizer ->
                customizer.additionalParameters(params ->
                        params.put("audience", "https://api.mfa01.com")
                )
        );

        return resolver;
    }
}
