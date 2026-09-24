package com.example.demo.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserAuditListner implements AuditorAware<String>{

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || !auth.isAuthenticated() || "admin".equals(auth.getPrincipal())){
            return Optional.of("SYSTEM");
        }

        return Optional.of(auth.getName());
    }

}
