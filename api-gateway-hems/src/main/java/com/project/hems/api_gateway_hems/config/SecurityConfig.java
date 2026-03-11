//package com.project.hems.api_gateway_hems.config;
//
//import com.project.hems.api_gateway_hems.filter.SessionValidationWebFilter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseCookie;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
//import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
//import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
//import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
//import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
//import reactor.core.publisher.Mono;
//
//import java.net.URI;
//import java.time.Duration;
//
//@RequiredArgsConstructor
//@Configuration
//public class SecurityConfig {
//
//
//    private final ReactiveOAuth2AuthorizedClientService clientService;
//
//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(
//            ServerHttpSecurity http,
//            ReactiveClientRegistrationRepository clientRegistrationRepository,
//            SessionValidationWebFilter sessionValidationWebFilter
//    ) {
//
//        ServerAuthenticationSuccessHandler successHandler =
//                (webFilterExchange, authentication) -> {
//                    if (!(authentication instanceof OAuth2AuthenticationToken oauth)) {
//                        return Mono.error(new IllegalStateException("Not an OAuth2AuthenticationToken"));
//                    }
//
//                    return clientService.loadAuthorizedClient(
//                                    oauth.getAuthorizedClientRegistrationId(),
//                                    oauth.getName())
//                            .flatMap(client -> {
//                                String token = client.getAccessToken().getTokenValue();
//
//                                ResponseCookie cookie = ResponseCookie.from("jwt", token)
//                                        .httpOnly(true)
//                                        .secure(true)
//                                        .sameSite("Strict")
//                                        .path("/")
//                                        .maxAge(Duration.ofHours(24))
//                                        .build();
//                                System.out.println("cookie" +cookie);
//                                webFilterExchange.getExchange().getResponse().addCookie(cookie);
//                                webFilterExchange.getExchange().getResponse()
//                                        .setStatusCode(HttpStatus.FOUND);
//                                webFilterExchange.getExchange().getResponse()
//                                        .getHeaders()
//                                        .setLocation(URI.create("/auth/create-user"));
//
//                                return webFilterExchange.getExchange().getResponse().setComplete();
//                            });
//                };
//        ServerAuthenticationFailureHandler failureHandler =
//                (webFilterExchange, exception) -> {
//
//                    System.out.println("LOGIN FAILED: " + exception.getMessage());
//
//                    webFilterExchange.getExchange().getResponse()
//                            .setStatusCode(HttpStatus.FOUND);
//                    webFilterExchange.getExchange().getResponse()
//                            .getHeaders()
//                            .setLocation(URI.create("/error"));
//
//                    return webFilterExchange.getExchange().getResponse().setComplete();
//                };
//
//        ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver =
//                authorizationRequestResolver(clientRegistrationRepository);
//
//        http
//                .authorizeExchange(exchanges -> exchanges
//                        .pathMatchers(
//                                "/swagger-ui/**",
//                                "/v3/api-docs/**",
//                                "/login",
//                                "/auth/start-login",
//                                "/debug/token",
//                                "/password-setup",
//                                "/auth/debug-c3",
//                                "/auth/send-password-setup-email",
//                                "/auth/check-email-and-handle",
//                                "/session/activate",
//                                "/login/**",
//                                "/oauth2/**"
//                        ).permitAll()
//                        .anyExchange().authenticated()
//                )
//                .oauth2Login(oauth2 -> oauth2
//                        .authorizationRequestResolver(authorizationRequestResolver)
//                        //.authenticationSuccessHandler(new OAuth2LoginSuccessHandler(clientService))
//                        .authenticationSuccessHandler(successHandler)
//                        .authenticationFailureHandler(failureHandler)
//                )
//                .oauth2ResourceServer(oauth2 ->
//                        oauth2.jwt(Customizer.withDefaults())
//                )
//                .addFilterAfter(sessionValidationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);
//
//        return http.build();
//    }
//
//
//    @Bean
//    public ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver(
//            ReactiveClientRegistrationRepository clientRegistrationRepository
//    ) {
//
//        DefaultServerOAuth2AuthorizationRequestResolver resolver =
//                new DefaultServerOAuth2AuthorizationRequestResolver(
//                        clientRegistrationRepository
//                );
//
//        resolver.setAuthorizationRequestCustomizer(customizer ->
//                customizer.additionalParameters(params ->
//                        params.put("audience", "https://api.mfa01.com")
//                )
//        );
//
//        return resolver;
//    }
//}

//---------------
package com.project.hems.api_gateway_hems.config;

import com.project.hems.api_gateway_hems.filter.SessionValidationWebFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

        private final ReactiveOAuth2AuthorizedClientService clientService;

        @Bean
        public SecurityWebFilterChain springSecurityFilterChain(
                        ServerHttpSecurity http,
                        ReactiveClientRegistrationRepository clientRegistrationRepository,
                        SessionValidationWebFilter sessionValidationWebFilter) {

                // OAuth2 Success Handler (for external OAuth2 providers like Google, Auth0,
                // etc.)
                ServerAuthenticationSuccessHandler oauthSuccessHandler = (webFilterExchange, authentication) -> {
                        if (!(authentication instanceof OAuth2AuthenticationToken oauth)) {
                                return Mono.error(new IllegalStateException("Not an OAuth2AuthenticationToken"));
                        }

                        return clientService.loadAuthorizedClient(
                                        oauth.getAuthorizedClientRegistrationId(),
                                        oauth.getName())
                                        .flatMap(client -> {
                                                String token = client.getAccessToken().getTokenValue();

                                                // set jwt in cookie and send to frontend
                                                ResponseCookie cookie = ResponseCookie.from("jwt", token)
                                                                .httpOnly(true)
                                                                .secure(true)
                                                                .sameSite("Strict")
                                                                .path("/")
                                                                .maxAge(Duration.ofHours(24))
                                                                .build();
                                                System.out.println("setting cookie for OAuth2 login: " + cookie);
                                                webFilterExchange.getExchange().getResponse().addCookie(cookie);

                                                // redirect to a specific page after successful OAuth login
                                                webFilterExchange.getExchange().getResponse()
                                                                .setStatusCode(HttpStatus.FOUND);
                                                webFilterExchange.getExchange().getResponse()
                                                                .getHeaders()
                                                                .setLocation(URI.create("/auth/create-user"));

                                                return webFilterExchange.getExchange().getResponse().setComplete();
                                        });
                };

                // Authentication failure handler
                ServerAuthenticationFailureHandler failureHandler = (webFilterExchange, exception) -> {
                        System.out.println("LOGIN FAILED: " + exception.getMessage());
                        webFilterExchange.getExchange().getResponse()
                                        .setStatusCode(HttpStatus.FOUND);
                        webFilterExchange.getExchange().getResponse()
                                        .getHeaders()
                                        .setLocation(URI.create("/error"));
                        return webFilterExchange.getExchange().getResponse().setComplete();
                };

                ServerLogoutSuccessHandler logoutSuccessHandler = (exchange, authentication) -> {

                        String AUTH0_LOGOUT_URL = "https://dev-0x5bg1uy1egiz4y0.us.auth0.com/v2/logout?returnTo=http://localhost:8090&client_id=esYKYGPnPt7qvkn8gZ7Qt1WI4IcNkEx8";

                        ResponseCookie deleteCookie = ResponseCookie.from("jwt", "")
                                        .httpOnly(true)
                                        .secure(true)
                                        .path("/")
                                        .maxAge(Duration.ZERO)
                                        .build();

                        var response = exchange.getExchange().getResponse();
                        response.addCookie(deleteCookie);

                        response.setStatusCode(HttpStatus.FOUND);
                        response.getHeaders().setLocation(URI.create(AUTH0_LOGOUT_URL));

                        return response.setComplete();
                };

                // Authorization request resolver for OAuth2 (auth0, google, github, etc.)
                ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver = authorizationRequestResolver(
                                clientRegistrationRepository);

                http
                                .authorizeExchange(exchanges -> exchanges
                                                .pathMatchers(
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**",
                                                                "/login",
                                                                "/auth/start-login",
                                                                "/debug/token",
                                                                "/password-setup",
                                                                "/auth/debug-c3",
                                                                "/auth/send-password-setup-email",
                                                                "/auth/check-email-and-handle",
                                                                "/session/activate",
                                                                "/login/**",
                                                                "/oauth2/**")
                                                .permitAll()
                                                .anyExchange().authenticated())
                                // OAuth2 login config
                                .oauth2Login(oauth2 -> oauth2
                                                .authorizationRequestResolver(authorizationRequestResolver)
                                                .authenticationSuccessHandler(oauthSuccessHandler)
                                                .authenticationFailureHandler(failureHandler))
                                // Use JWT for resource server authentication
                                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                                // Add session validation filter
                                .addFilterAfter(sessionValidationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessHandler(logoutSuccessHandler));

                return http.build();
        }

        // Custom authorization request resolver for Auth0 and other OAuth2 providers
        @Bean
        public ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver(
                        ReactiveClientRegistrationRepository clientRegistrationRepository) {
                DefaultServerOAuth2AuthorizationRequestResolver resolver = new DefaultServerOAuth2AuthorizationRequestResolver(
                                clientRegistrationRepository);

                resolver.setAuthorizationRequestCustomizer(customizer -> customizer
                                .additionalParameters(params -> params.put("audience", "https://api.mfa01.com")));

                return resolver;
        }
}