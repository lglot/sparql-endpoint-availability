package it.unife.sparql_endpoint_availability.controller;

import it.unife.sparql_endpoint_availability.exception.UserAlreadyExistsException;
import it.unife.sparql_endpoint_availability.jwt.JwtConfig;
import it.unife.sparql_endpoint_availability.model.management.AppUserManagement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JwtAuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final JwtConfig jwtConfig;

    private final AppUserManagement appUserManagement;

    @Autowired
    public JwtAuthenticationControllerTest(JwtConfig jwtConfig, AppUserManagement appUserManagement) {
        this.jwtConfig = jwtConfig;
        this.appUserManagement = appUserManagement;
    }

    @BeforeAll
    public void createUser() throws UserAlreadyExistsException {
        appUserManagement.saveUser("test", "test", "user");
    }

    @Test
    void login_should_return_jwtToken() throws Exception {
        try {
            mockMvc.perform(post("/api/login")
                    .contentType("application/json")
                    .content("{\"username\":\"test\",\"password\":\"test\"}"))
                    .andExpect(status().isOk())
                    .andExpect(result -> {
                        assertTrue(result.getResponse().getContentAsString().contains(jwtConfig.getPrefix()));
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void login_should_return_unauthorized_if_credentials_are_wrong() throws Exception {
        try {
            mockMvc.perform(post("/api/login")
                    .contentType("application/json")
                    .content("{\"username\":\"test\",\"password\":\"wrong\"}"))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}