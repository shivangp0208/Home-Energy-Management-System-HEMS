////package com.project.hems.api_gateway_hems.config;
////
////import com.project.hems.api_gateway_hems.filter.SessionValidationWebFilter;
////import lombok.RequiredArgsConstructor;
////import org.springframework.context.annotation.Bean;
////import org.springframework.context.annotation.Configuration;
////import org.springframework.http.HttpStatus;
////import org.springframework.http.ResponseCookie;
////import org.springframework.security.config.Customizer;
////import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
////import org.springframework.security.config.web.server.ServerHttpSecurity;
////import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
////import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
////import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
////import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
////import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
////import org.springframework.security.oauth2.core.oidc.user.OidcUser;
////import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
////import org.springframework.security.web.server.SecurityWebFilterChain;
////import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
////import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
////import reactor.core.publisher.Mono;
////
////import java.net.URI;
////import java.time.Duration;
////
////@RequiredArgsConstructor
////@Configuration
////public class SecurityConfig {
////
////
////    private final ReactiveOAuth2AuthorizedClientService clientService;
////
////    @Bean
////    public SecurityWebFilterChain springSecurityFilterChain(
////            ServerHttpSecurity http,
////            ReactiveClientRegistrationRepository clientRegistrationRepository,
////            SessionValidationWebFilter sessionValidationWebFilter
////    ) {
////
////        ServerAuthenticationSuccessHandler successHandler =
////                (webFilterExchange, authentication) -> {
////                    if (!(authentication instanceof OAuth2AuthenticationToken oauth)) {
////                        return Mono.error(new IllegalStateException("Not an OAuth2AuthenticationToken"));
////                    }
////
////                    return clientService.loadAuthorizedClient(
////                                    oauth.getAuthorizedClientRegistrationId(),
////                                    oauth.getName())
////                            .flatMap(client -> {
////                                String token = client.getAccessToken().getTokenValue();
////
////                                ResponseCookie cookie = ResponseCookie.from("jwt", token)
////                                        .httpOnly(true)
////                                        .secure(true)
////                                        .sameSite("Strict")
////                                        .path("/")
////                                        .maxAge(Duration.ofHours(24))
////                                        .build();
////                                System.out.println("cookie" +cookie);
////                                webFilterExchange.getExchange().getResponse().addCookie(cookie);
////                                webFilterExchange.getExchange().getResponse()
////                                        .setStatusCode(HttpStatus.FOUND);
////                                webFilterExchange.getExchange().getResponse()
////                                        .getHeaders()
////                                        .setLocation(URI.create("/auth/create-user"));
////
////                                return webFilterExchange.getExchange().getResponse().setComplete();
////                            });
////                };
////        ServerAuthenticationFailureHandler failureHandler =
////                (webFilterExchange, exception) -> {
////
////                    System.out.println("LOGIN FAILED: " + exception.getMessage());
////
////                    webFilterExchange.getExchange().getResponse()
////                            .setStatusCode(HttpStatus.FOUND);
////                    webFilterExchange.getExchange().getResponse()
////                            .getHeaders()
////                            .setLocation(URI.create("/error"));
////
////                    return webFilterExchange.getExchange().getResponse().setComplete();
////                };
////
////        ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver =
////                authorizationRequestResolver(clientRegistrationRepository);
////
////        http
////                .authorizeExchange(exchanges -> exchanges
////                        .pathMatchers(
////                                "/swagger-ui/**",
////                                "/v3/api-docs/**",
////                                "/login",
////                                "/auth/start-login",
////                                "/debug/token",
////                                "/password-setup",
////                                "/auth/debug-c3",
////                                "/auth/send-password-setup-email",
////                                "/auth/check-email-and-handle",
////                                "/session/activate",
////                                "/login/**",
////                                "/oauth2/**"
////                        ).permitAll()
////                        .anyExchange().authenticated()
////                )
////                .oauth2Login(oauth2 -> oauth2
////                        .authorizationRequestResolver(authorizationRequestResolver)
////                        //.authenticationSuccessHandler(new OAuth2LoginSuccessHandler(clientService))
////                        .authenticationSuccessHandler(successHandler)
////                        .authenticationFailureHandler(failureHandler)
////                )
////                .oauth2ResourceServer(oauth2 ->
////                        oauth2.jwt(Customizer.withDefaults())
////                )
////                .addFilterAfter(sessionValidationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);
////
////        return http.build();
////    }
////
////
////    @Bean
////    public ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver(
////            ReactiveClientRegistrationRepository clientRegistrationRepository
////    ) {
////
////        DefaultServerOAuth2AuthorizationRequestResolver resolver =
////                new DefaultServerOAuth2AuthorizationRequestResolver(
////                        clientRegistrationRepository
////                );
////
////        resolver.setAuthorizationRequestCustomizer(customizer ->
////                customizer.additionalParameters(params ->
////                        params.put("audience", "https://api.mfa01.com")
////                )
////        );
////
////        return resolver;
////    }
////}
//
////---------------
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
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
//import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
//import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
//
//import reactor.core.publisher.Mono;
//
//import java.net.URI;
//import java.time.Duration;
//
//@RequiredArgsConstructor
//@Configuration
//public class SecurityConfig {
//
//        private final ReactiveOAuth2AuthorizedClientService clientService;
//
//        @Bean
//        public SecurityWebFilterChain springSecurityFilterChain(
//                        ServerHttpSecurity http,
//                        ReactiveClientRegistrationRepository clientRegistrationRepository,
//                        SessionValidationWebFilter sessionValidationWebFilter) {
//
//                // OAuth2 Success Handler (for external OAuth2 providers like Google, Auth0,
//                // etc.)
//                ServerAuthenticationSuccessHandler oauthSuccessHandler = (webFilterExchange, authentication) -> {
//                        if (!(authentication instanceof OAuth2AuthenticationToken oauth)) {
//                                return Mono.error(new IllegalStateException("Not an OAuth2AuthenticationToken"));
//                        }
//
//                        return clientService.loadAuthorizedClient(
//                                        oauth.getAuthorizedClientRegistrationId(),
//                                        oauth.getName())
//                                        .flatMap(client -> {
//                                                String token = client.getAccessToken().getTokenValue();
//
//                                                // set jwt in cookie and send to frontend
//                                                ResponseCookie cookie = ResponseCookie.from("jwt", token)
//                                                                .httpOnly(true)
//                                                                .secure(true)
//                                                                .sameSite("Strict")
//                                                                .path("/")
//                                                                .maxAge(Duration.ofHours(24))
//                                                                .build();
//                                                System.out.println("setting cookie for OAuth2 login: " + cookie);
//                                                webFilterExchange.getExchange().getResponse().addCookie(cookie);
//
//                                                // redirect to a specific page after successful OAuth login
//                                                webFilterExchange.getExchange().getResponse()
//                                                                .setStatusCode(HttpStatus.FOUND);
//                                                webFilterExchange.getExchange().getResponse()
//                                                                .getHeaders()
//                                                                .setLocation(URI.create("/auth/create-user"));
//
//                                                return webFilterExchange.getExchange().getResponse().setComplete();
//                                        });
//                };
//
//                // Authentication failure handler
//                ServerAuthenticationFailureHandler failureHandler = (webFilterExchange, exception) -> {
//                        System.out.println("LOGIN FAILED: " + exception.getMessage());
//                        webFilterExchange.getExchange().getResponse()
//                                        .setStatusCode(HttpStatus.FOUND);
//                        webFilterExchange.getExchange().getResponse()
//                                        .getHeaders()
//                                        .setLocation(URI.create("/error"));
//                        return webFilterExchange.getExchange().getResponse().setComplete();
//                };
//
//                ServerLogoutSuccessHandler logoutSuccessHandler = (exchange, authentication) -> {
//
//                        String AUTH0_LOGOUT_URL = "https://dev-0x5bg1uy1egiz4y0.us.auth0.com/v2/logout?returnTo=http://localhost:8090&client_id=esYKYGPnPt7qvkn8gZ7Qt1WI4IcNkEx8";
//
//                        ResponseCookie deleteCookie = ResponseCookie.from("jwt", "")
//                                        .httpOnly(true)
//                                        .secure(true)
//                                        .path("/")
//                                        .maxAge(Duration.ZERO)
//                                        .build();
//
//                        var response = exchange.getExchange().getResponse();
//                        response.addCookie(deleteCookie);
//
//                        response.setStatusCode(HttpStatus.FOUND);
//                        response.getHeaders().setLocation(URI.create(AUTH0_LOGOUT_URL));
//
//                        return response.setComplete();
//                };
//
//                // Authorization request resolver for OAuth2 (auth0, google, github, etc.)
//                ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver = authorizationRequestResolver(
//                                clientRegistrationRepository);
//
//                http
//                                .authorizeExchange(exchanges -> exchanges
//                                                .pathMatchers(
//                                                                "/swagger-ui/**",
//                                                                "/v3/api-docs/**",
//                                                                "/login",
//                                                                "/auth/start-login",
//                                                                "/debug/token",
//                                                                "/password-setup",
//                                                                "/auth/debug-c3",
//                                                                "/auth/send-password-setup-email",
//                                                                "/auth/check-email-and-handle",
//                                                                "/session/activate",
//                                                                "/login/**",
//                                                                "/oauth2/**")
//                                                .permitAll()
//                                                .anyExchange().authenticated())
//                                // OAuth2 login config
//                                .oauth2Login(oauth2 -> oauth2
//                                                .authorizationRequestResolver(authorizationRequestResolver)
//                                                .authenticationSuccessHandler(oauthSuccessHandler)
//                                                .authenticationFailureHandler(failureHandler))
//                                // Use JWT for resource server authentication
//                                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//                                // Add session validation filter
//                                .addFilterAfter(sessionValidationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
//                                .logout(logout -> logout
//                                                .logoutUrl("/logout")
//                                                .logoutSuccessHandler(logoutSuccessHandler));
//
//                return http.build();
//        }
//
//        // Custom authorization request resolver for Auth0 and other OAuth2 providers
//        @Bean
//        public ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver(
//                        ReactiveClientRegistrationRepository clientRegistrationRepository) {
//                DefaultServerOAuth2AuthorizationRequestResolver resolver = new DefaultServerOAuth2AuthorizationRequestResolver(
//                                clientRegistrationRepository);
//
//                resolver.setAuthorizationRequestCustomizer(customizer -> customizer
//                                .additionalParameters(params -> params.put("audience", "https://api.mfa01.com")));
//
//                return resolver;
//        }
//}


package com.project.hems.api_gateway_hems.config;

import com.project.hems.api_gateway_hems.filter.SessionValidationWebFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

/**
 * Security Configuration for API Gateway with JWT, OAuth2, and Session Management
 *
 * Features:
 * - OAuth2 Login with Auth0, Google, GitHub, etc.
 * - JWT Token validation via Bearer tokens
 * - Session validation via custom filter
 * - Logout with Auth0 integration
 * - Graceful handling when OAuth2 is not configured
 */
@Slf4j
@Configuration
public class SecurityConfig {

    /**
     * Main security filter chain configuration
     *
     * @param http ServerHttpSecurity to configure
     * @param clientService OAuth2 authorized client service (optional)
     * @param clientRegistrationRepository OAuth2 client registration (optional)
     * @param sessionValidationWebFilter Custom session validation filter
     * @return Configured SecurityWebFilterChain
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            @Nullable ReactiveOAuth2AuthorizedClientService clientService,
            @Nullable ReactiveClientRegistrationRepository clientRegistrationRepository,
            SessionValidationWebFilter sessionValidationWebFilter) {

        log.info("[v0] Configuring security filter chain with OAuth2 and JWT");

        // OAuth2 Success Handler - called after successful OAuth2 login
        ServerAuthenticationSuccessHandler oauthSuccessHandler = (webFilterExchange, authentication) -> {
            log.info("[v0] OAuth2 login successful for user: {}", authentication.getName());

            // If clientService is not available, just redirect to frontend
            if (clientService == null) {
                log.warn("[v0] ReactiveOAuth2AuthorizedClientService not available, redirecting without token");
                webFilterExchange.getExchange().getResponse()
                        .setStatusCode(HttpStatus.FOUND);
                webFilterExchange.getExchange().getResponse()
                        .getHeaders()
                        .setLocation(URI.create("/auth/create-user"));
                return webFilterExchange.getExchange().getResponse().setComplete();
            }

            if (!(authentication instanceof OAuth2AuthenticationToken oauth)) {
                log.error("[v0] Authentication is not OAuth2AuthenticationToken");
                return Mono.error(new IllegalStateException("Not an OAuth2AuthenticationToken"));
            }

            return clientService.loadAuthorizedClient(
                            oauth.getAuthorizedClientRegistrationId(),
                            oauth.getName())
                    .doOnNext(client -> log.info("[v0] Loaded authorized client for: {}",
                            oauth.getAuthorizedClientRegistrationId()))
                    .flatMap(client -> {
                        String token = client.getAccessToken().getTokenValue();

                        // Set JWT token in secure HTTP-only cookie
                        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                                .httpOnly(true)
                                .secure(true)
                                .sameSite("Strict")
                                .path("/")
                                .maxAge(Duration.ofHours(24))
                                .build();

                        log.info("[v0] Setting JWT cookie for OAuth2 login");
                        webFilterExchange.getExchange().getResponse().addCookie(cookie);

                        // Redirect to frontend app after successful login
                        webFilterExchange.getExchange().getResponse()
                                .setStatusCode(HttpStatus.FOUND);
                        webFilterExchange.getExchange().getResponse()
                                .getHeaders()
                                .setLocation(URI.create("/auth/create-user"));

                        return webFilterExchange.getExchange().getResponse().setComplete();
                    })
                    .doOnError(error -> log.error("[v0] OAuth2 login error", error));
        };

        // Authentication failure handler - called when OAuth2 login fails
        ServerAuthenticationFailureHandler failureHandler = (webFilterExchange, exception) -> {
            log.warn("[v0] OAuth2 login failed: {}", exception.getMessage());

            webFilterExchange.getExchange().getResponse()
                    .setStatusCode(HttpStatus.FOUND);
            webFilterExchange.getExchange().getResponse()
                    .getHeaders()
                    .setLocation(URI.create("/error"));

            return webFilterExchange.getExchange().getResponse().setComplete();
        };

        // Logout success handler - clears JWT cookie and redirects to Auth0 logout
        ServerLogoutSuccessHandler logoutSuccessHandler = (exchange, authentication) -> {
            log.info("[v0] User logging out");

            // Auth0 logout endpoint (replace with your Auth0 domain and settings)
            String AUTH0_LOGOUT_URL = "https://dev-0x5bg1uy1egiz4y0.us.auth0.com/v2/logout?returnTo=http://localhost:8090&client_id=esYKYGPnPt7qvkn8gZ7Qt1WI4IcNkEx8";

            // Delete JWT cookie
            ResponseCookie deleteCookie = ResponseCookie.from("jwt", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(Duration.ZERO)
                    .build();

            var response = exchange.getExchange().getResponse();
            response.addCookie(deleteCookie);

            // Redirect to Auth0 logout
            response.setStatusCode(HttpStatus.FOUND);
            response.getHeaders().setLocation(URI.create(AUTH0_LOGOUT_URL));

            return response.setComplete();
        };

        // Build authorization request resolver only if OAuth2 is configured
        ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver;
        if (clientRegistrationRepository != null) {
            log.info("[v0] OAuth2 client registration repository found, configuring resolver");
            authorizationRequestResolver = authorizationRequestResolver(clientRegistrationRepository);
        } else {
            authorizationRequestResolver = null;
            log.warn("[v0] OAuth2 client registration repository not found, OAuth2 login may not work");
        }

        // Configure security rules and filters
        http
                // Authorization rules
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(
                                // Swagger/OpenAPI documentation
                                "/swagger-ui/**",
                                "/v3/api-docs/**",

                                // OAuth2 and login endpoints
                                "/login",
                                "/login/**",
                                "/oauth2/**",
                                "/auth/start-login",

                                // Public endpoints for password setup and session
                                "/password-setup",
                                "/auth/send-password-setup-email",
                                "/auth/check-email-and-handle",
                                "/session/activate",

                                // Debug endpoints (remove in production)
                                "/debug/token",
                                "/auth/debug-c3"
                        )
                        .permitAll()
                        .anyExchange().authenticated())

                // OAuth2 login configuration (if OAuth2 is configured)
                .oauth2Login(oauth2 -> {
                    oauth2.authenticationSuccessHandler(oauthSuccessHandler)
                            .authenticationFailureHandler(failureHandler);

                    if (authorizationRequestResolver != null) {
                        oauth2.authorizationRequestResolver(authorizationRequestResolver);
                    }
                })

                // JWT token validation for Bearer tokens
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))

                // Add custom session validation filter
                .addFilterAfter(sessionValidationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                // Logout configuration
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler));

        return http.build();
    }

    /**
     * Creates authorization request resolver for OAuth2 providers
     * Adds custom parameters like 'audience' for Auth0
     *
     * @param clientRegistrationRepository Repository with OAuth2 client registrations
     * @return Configured ServerOAuth2AuthorizationRequestResolver
     */
    @Bean
    public ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ReactiveClientRegistrationRepository clientRegistrationRepository) {

        log.info("[v0] Creating OAuth2 authorization request resolver");

        DefaultServerOAuth2AuthorizationRequestResolver resolver =
                new DefaultServerOAuth2AuthorizationRequestResolver(clientRegistrationRepository);

        // Add custom parameters to authorization request (e.g., Auth0 audience)
        resolver.setAuthorizationRequestCustomizer(customizer ->
                customizer.additionalParameters(params ->
                        params.put("audience", "https://api.mfa01.com")
                )
        );

        log.info("[v0] OAuth2 authorization request resolver configured with audience parameter");
        return resolver;
    }
}