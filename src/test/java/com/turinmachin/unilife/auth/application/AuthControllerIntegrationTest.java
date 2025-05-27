package com.turinmachin.unilife.auth.application;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turinmachin.unilife.PostgresContainerConfig;
import com.turinmachin.unilife.authentication.dto.JwtAuthLoginDto;
import com.turinmachin.unilife.user.dto.RegisterUserDto;

@SpringBootTest
@AutoConfigureMockMvc
@Import(PostgresContainerConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String EMAIL = "juan@mail.com";
    private final String USERNAME = "juan";
    private final String PASSWORD = "1234";

    @Test
    @Order(1)
    public void testRegister() throws Exception {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setEmail(EMAIL);
        dto.setUsername(USERNAME);
        dto.setPassword(PASSWORD);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(jsonPath("$.verified").value(false))
                .andExpect(jsonPath("$.password").doesNotExist()); // porsiaca

        // Duplicate email/username should return conflict
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(2)
    public void testLoginByEmail() throws Exception {
        JwtAuthLoginDto dto = new JwtAuthLoginDto();
        dto.setUsername(EMAIL);
        dto.setPassword(PASSWORD);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    @Order(3)
    public void testLoginByUsername() throws Exception {
        JwtAuthLoginDto dto = new JwtAuthLoginDto();
        dto.setUsername(USERNAME);
        dto.setPassword(PASSWORD);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    @Order(4)
    public void testInvalidLogin() throws Exception {
        JwtAuthLoginDto dto = new JwtAuthLoginDto();
        dto.setUsername(USERNAME);
        dto.setPassword("not_a_valid_password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

}