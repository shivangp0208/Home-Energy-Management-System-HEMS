package com.project.hems.auth_service_hems.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/error", "/api/auth",
                                "/swagger-ui/**",
                                "/v3/api-docs/**").permitAll()
                        .requestMatchers("/checkeddata").authenticated()
                        .anyRequest().permitAll())
                // .oauth2Login(oauth2 -> oauth2
                // .authorizationEndpoint(auth -> auth
                // .authorizationRequestResolver(
                // authorizationRequestResolver(clientRegistrationRepository)
                // )
                // )
                // )

                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .logout(logout -> logout.logoutSuccessUrl("/"));

        return http.build();
    }

    // @Bean
    // OAuth2AuthorizationRequestResolver
    // authorizationRequestResolver(ClientRegistrationRepository
    // clientRegistrationRepository) {
    // DefaultOAuth2AuthorizationRequestResolver resolver =
    // new DefaultOAuth2AuthorizationRequestResolver(
    // clientRegistrationRepository,
    // "/oauth2/authorization"
    // );
    //
    // resolver.setAuthorizationRequestCustomizer(customizer ->
    // customizer.additionalParameters(params ->
    // params.put("audience", "https://api.mfa01.com")
    // )
    // );
    //
    // return resolver;
    // }

}
