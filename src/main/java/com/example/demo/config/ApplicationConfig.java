package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.repositories.UserRepository;

@Configuration
public class ApplicationConfig {

        private final UserRepository userRepository;

        @Autowired
        public ApplicationConfig(UserRepository userRepository) {
                this.userRepository = userRepository;
        }

        // InMemoryUserDetailsManager
        // @Bean
        // public UserDetailsManager userServiceDetailsManager() {

        // UserDetails admin = User.withUsername(adminUsername)
        // .password("{noop}" + adminPassword)
        // .roles("ADMIN")
        // .build();

        // UserDetails user = User.withUsername(customerUsername)
        // .password("{noop}" + customerPassword)
        // .roles("USER")
        // .build();

        // return new InMemoryUserDetailsManager(admin, user);
        // }

        @Bean
        UserDetailsService userDetailsService() {
                return username -> userRepository.findByEmail(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

}
