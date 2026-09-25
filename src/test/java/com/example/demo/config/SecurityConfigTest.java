package com.example.demo.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminEndpoint_withoutAuthentication_isUnauthorized() throws Exception {
        mockMvc.perform(get("/admin/retriveAllItems")).andExpect(status().isUnauthorized());
    }

    @Test
    void customerEndpoint_withoutAuthentication_isUnauthorized() throws Exception {
        mockMvc.perform(get("/customer/getItemById/1")).andExpect(status().isUnauthorized());
    }

    @Test
    void userEndpoint_withoutAuthentication_isUnauthorized() throws Exception {
        mockMvc.perform(get("/user/anything")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void adminEndpoint_withCustomerRole_isForbidden() throws Exception {
        mockMvc.perform(get("/admin/retriveAllItems")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "AGENT")
    void adminEndpoint_withAgentRole_isForbidden() throws Exception {
        mockMvc.perform(get("/admin/retriveAllItems")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminEndpoint_withAdminRole_isNotBlockedBySecurity() throws Exception {
        mockMvc.perform(get("/admin/retriveAllItems"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.assertj.core.api.Assertions.assertThat(status).isNotIn(401, 403);
                });
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerEndpoint_withCustomerRole_isNotBlockedBySecurity() throws Exception {
        mockMvc.perform(get("/customer/getItemById/1"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.assertj.core.api.Assertions.assertThat(status).isNotIn(401, 403);
                });
    }

    @Test
    void loginEndpoint_isReachableWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/auth/login").contentType("application/json").content("{}"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.assertj.core.api.Assertions.assertThat(status).isNotIn(401, 403);
                });
    }

    @Test
    void signupEndpoint_withoutAuthentication_isUnauthorized() throws Exception {
        mockMvc.perform(post("/auth/signup").contentType("application/json").content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "customer", roles = "CUSTOMER")
    void signupEndpoint_withCustomerRole_isNotBlockedBySecurity() throws Exception {
        mockMvc.perform(post("/auth/signup").contentType("application/json").content("{}"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.assertj.core.api.Assertions.assertThat(status).isNotIn(401, 403);
                });
    }
}
