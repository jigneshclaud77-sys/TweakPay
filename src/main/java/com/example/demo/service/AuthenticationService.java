package com.example.demo.service;

import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.LoginUserDto;
import com.example.demo.dto.RegisterUserDto;
import com.example.demo.entity.RoleType;
import com.example.demo.entity.User;
import com.example.demo.repositories.UserRepository;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    
    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public User signup(RegisterUserDto input, Authentication authentication){
        System.out.print(authentication.getPrincipal());
        final Set<RoleType> role = switch (authentication.getName()) {
            case "customer" -> Set.of(RoleType.CUSTOMER);
            case "admin" -> Set.of(RoleType.ADMIN, RoleType.CUSTOMER);
            default -> throw new IllegalArgumentException("Unsupported principal");
        };
        
        User user = User.builder().fullName(input.getFullName()).email(input.getEmail()).password(passwordEncoder.encode(input.getPassword())).roles(role).build();
        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));
        return userRepository.findByEmail(input.getEmail()).orElseThrow();
    }

}
