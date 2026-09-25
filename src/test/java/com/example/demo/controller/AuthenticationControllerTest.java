package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.LoginUserDto;
import com.example.demo.entity.RoleType;
import com.example.demo.entity.User;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Pure controller/validation slice test: security filters are disabled here because
 * {@code @WebMvcTest} does not pick up the project's own {@code SecurityConfig} bean,
 * so leaving filters on would exercise Spring Boot's unrelated default security
 * auto-configuration instead. Authorization behavior is covered by SecurityConfigTest.
 */
@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void login_withValidCredentials_returnsOkWithToken() throws Exception {
        LoginUserDto loginDto = new LoginUserDto();
        loginDto.setEmail("user@example.com");
        loginDto.setPassword("Password123");

        User authenticatedUser = User.builder().email(loginDto.getEmail()).roles(java.util.Set.of(RoleType.CUSTOMER))
                .build();

        when(authenticationService.authenticate(any())).thenReturn(authenticatedUser);
        when(jwtService.generateToken(any())).thenReturn("jwt-token");
        when(jwtService.getJwtExpiration()).thenReturn(3600000L);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.expiresIn").value(3600000));
    }

    @Test
    void login_withBlankEmail_returnsBadRequest() throws Exception {
        LoginUserDto loginDto = new LoginUserDto();
        loginDto.setEmail("");
        loginDto.setPassword("Password123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withMalformedEmail_returnsBadRequest() throws Exception {
        LoginUserDto loginDto = new LoginUserDto();
        loginDto.setEmail("not-an-email");
        loginDto.setPassword("Password123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signup_withValidBody_returnsOk() throws Exception {
        User newUser = User.builder().email("new@example.com").roles(java.util.Set.of(RoleType.CUSTOMER)).build();
        when(authenticationService.signup(any(), any())).thenReturn(newUser);

        String body = """
                {"email":"new@example.com","password":"Password123","fullName":"New User"}
                """;

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void signup_withBlankFullName_returnsBadRequest() throws Exception {
        String body = """
                {"email":"new@example.com","password":"Password123","fullName":""}
                """;

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }
}
