package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dto.LoginUserDto;
import com.example.demo.dto.RegisterUserDto;
import com.example.demo.entity.RoleType;
import com.example.demo.entity.User;
import com.example.demo.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    private AuthenticationService authenticationService() {
        return new AuthenticationService(userRepository, passwordEncoder, authenticationManager);
    }

    private RegisterUserDto registerDto() {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setEmail("new.user@example.com");
        dto.setPassword("Password123");
        dto.setFullName("New User");
        return dto;
    }

    @Test
    void signup_assignsCustomerRole_whenCallerIsCustomer() {
        when(authentication.getName()).thenReturn("customer");
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authenticationService().signup(registerDto(), authentication);

        assertThat(result.getRoles()).containsExactly(RoleType.CUSTOMER);
    }

    @Test
    void signup_assignsAdminAndCustomerRoles_whenCallerIsAdmin() {
        when(authentication.getName()).thenReturn("admin");
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authenticationService().signup(registerDto(), authentication);

        assertThat(result.getRoles()).containsExactlyInAnyOrder(RoleType.ADMIN, RoleType.CUSTOMER);
    }

    @Test
    void signup_throwsIllegalArgumentException_whenCallerIsUnsupported() {
        when(authentication.getName()).thenReturn("someone-else");

        assertThatThrownBy(() -> authenticationService().signup(registerDto(), authentication))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void authenticate_delegatesToAuthenticationManager_andReturnsPersistedUser() {
        LoginUserDto loginDto = new LoginUserDto();
        loginDto.setEmail("existing.user@example.com");
        loginDto.setPassword("Password123");

        User persistedUser = User.builder().email(loginDto.getEmail()).build();
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(persistedUser));

        User result = authenticationService().authenticate(loginDto);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(result).isSameAs(persistedUser);
    }
}
