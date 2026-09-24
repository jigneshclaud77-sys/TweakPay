package com.example.demo.config;

import static com.example.demo.entity.RoleType.ADMIN;
import static com.example.demo.entity.RoleType.AGENT;
import static com.example.demo.entity.RoleType.CUSTOMER;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

@EnableMethodSecurity
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final HandlerExceptionResolver handlerExceptionResolver;

        public SecurityConfig(HandlerExceptionResolver handlerExceptionResolver) {
                this.handlerExceptionResolver = handlerExceptionResolver;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider,
                        JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

                return http.authorizeHttpRequests(
                                auth -> auth
                                                .requestMatchers("/auth/login").permitAll()
                                                .requestMatchers("/auth/signup").hasAnyRole(ADMIN.name(), CUSTOMER.name())
                                                .requestMatchers("/admin/*").hasRole(ADMIN.name())
                                                .requestMatchers("/user/*").hasAnyRole(CUSTOMER.name(), AGENT.name(), ADMIN.name())
                                                .anyRequest().authenticated())
                                .httpBasic(Customizer.withDefaults()).formLogin(form -> form.disable())
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .exceptionHandling(handle -> handle.accessDeniedHandler((request, response,
                                                accessDeniedException) -> handlerExceptionResolver.resolveException(
                                                                request, response, jwtAuthenticationFilter,
                                                                accessDeniedException)))
                                .build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(List.of("http://localhost:8080"));
                configuration.setAllowedMethods(List.of("GET", "POST"));
                configuration.setAllowedHeaders(List.of("Authorization", "Content-type"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
