package com.healthfix.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class for the application.
 * Configures authentication, authorization, and security filters.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true
)
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Array of public endpoints that don't require authentication.
     * Includes Swagger documentation endpoints and auth endpoints.
     */
    private static final String[] PUBLIC_MATCHER = {
            "/v3/api-docs/**",
            "/configuration/ui",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/configuration/security/**",
            "/swagger-ui/**",
            "/api/v1/auth/**",
            "/webjars/**"
    };

    /**
     * Configures the security filter chain for the application.
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(PUBLIC_MATCHER).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(httpSecurityHeadersConfigurer ->
                        httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) //to make accessible h2 console, it works as frame
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(new CustomAuthEntryPoint()))
                .addFilterBefore(new FirebaseAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // Set the custom AuthenticationManager for Firebase Authentication
        http.authenticationManager(customAuthenticationManager());
        return http.build();
    }

    /**
     * Creates a custom AuthenticationManager for Firebase authentication.
     * Sets the authentication in the SecurityContextHolder.
     *
     * @return the custom AuthenticationManager instance
     */
    @Bean
    public AuthenticationManager customAuthenticationManager() {
        return authentication -> {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        };
    }
}