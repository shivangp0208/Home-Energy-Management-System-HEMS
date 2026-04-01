package com.hems.project.virtual_power_plant.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity //so apde je @PreAuthorize lakhyu che e work karse aa annotation vagar e nai chale
public class
SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
        .csrf(csrf -> csrf.disable())
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
        .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated());

        return httpSecurity.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter converter =
                new JwtGrantedAuthoritiesConverter();

        //by default spring jwt mathi permission ne na jove e ena scope and authorities ma scope jove
        //so apde ene kai chiee ke authorities ma permission section che jwt ma e tamare jovvanu che
        converter.setAuthoritiesClaimName("permissions");
        //so by default spring permission ne SCOPE_permission_name em kri dey so apde ene kaiee chiee
        //ke SCOPE_ na jagyae kasu nai rakhta so we set prefix "" nothing
        converter.setAuthorityPrefix(""); // remove SCOPE_

        JwtAuthenticationConverter jwtConverter =
                new JwtAuthenticationConverter();

        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);

        return jwtConverter;
    }
}
