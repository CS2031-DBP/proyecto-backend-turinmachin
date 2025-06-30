package com.turinmachin.unilife.user.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turinmachin.unilife.PostgresContainerConfig;
import com.turinmachin.unilife.jwt.domain.JwtService;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import com.turinmachin.unilife.user.dto.RegisterUserDto;
import com.turinmachin.unilife.user.dto.UpdateUserDto;
import com.turinmachin.unilife.user.dto.UpdateUserPasswordDto;
import com.turinmachin.unilife.user.infrastructure.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Import(PostgresContainerConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User admin;
    private User user1;
    private User user2;
    private String adminAuth;
    private String auth1;
    private String auth2;

    @Test
    @Order(1)
    public void testGetAllUsers() throws Exception {
        // Check that only the default admin exists initially
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.[0].role").value("ADMIN"));

        admin = userRepository.findAll().getFirst();
        assertNotNull(admin);
        adminAuth = "Bearer " + jwtService.generateToken(admin);

        RegisterUserDto dto1 = new RegisterUserDto();
        dto1.setUsername("juan");
        dto1.setEmail("juan@mail.com");
        dto1.setPassword("1234");
        dto1.setDisplayName("Juan");
        user1 = userService.createUser(dto1);

        RegisterUserDto dto2 = new RegisterUserDto();
        dto2.setUsername("carlos");
        dto2.setEmail("carlos@mail.com");
        dto2.setPassword("1234");
        dto2.setDisplayName("Carlos");
        user2 = userService.createUser(dto2);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @Order(2)
    public void testGetSelf() throws Exception {
        mockMvc.perform(get("/users/@self"))
                .andExpect(status().is4xxClientError());

        auth1 = "Bearer " + jwtService.generateToken(user1);
        auth2 = "Bearer " + jwtService.generateToken(user2);

        mockMvc.perform(get("/users/@self").header("Authorization", auth1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user1.getUsername()));

        mockMvc.perform(get("/users/@self").header("Authorization", auth2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user2.getUsername()));
    }

    @Test
    @Order(3)
    public void testGetUserById() throws Exception {
        mockMvc.perform(get("/users/{id}", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user1.getUsername()))
                .andExpect(jsonPath("$.email").value(user1.getEmail()))
                .andExpect(jsonPath("$.displayName").value(user1.getDisplayName()));

        mockMvc.perform(get("/users/{id}", user2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user2.getUsername()))
                .andExpect(jsonPath("$.email").value(user2.getEmail()))
                .andExpect(jsonPath("$.displayName").value(user2.getDisplayName()));

        UUID nonExistentId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        mockMvc.perform(get("/users/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    public void testUpdateSelf() throws Exception {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setUsername(user1.getUsername());
        dto.setBio("Hello yall");
        dto.setDisplayName("JuanElCrack");

        mockMvc.perform(put("/users/@self")
                .header("Authorization", auth1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bio").value(dto.getBio()))
                .andExpect(jsonPath("$.displayName").value(dto.getDisplayName()));

        user1 = userRepository.findById(user1.getId()).orElseThrow();
        assertEquals(dto.getBio(), user1.getBio());
        assertEquals(dto.getDisplayName(), user1.getDisplayName());
    }

    @Test
    @Order(5)
    public void testUpdateSelfPassword() throws Exception {
        UpdateUserPasswordDto dto = new UpdateUserPasswordDto();
        dto.setNewPassword("123456");
        dto.setCurrentPassword("123");

        mockMvc.perform(patch("/users/@self/password")
                .header("Authorization", auth2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

        dto.setCurrentPassword("1234");

        mockMvc.perform(patch("/users/@self/password")
                .header("Authorization", auth2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        user2 = userRepository.findById(user2.getId()).orElseThrow();
        Assertions.assertTrue(passwordEncoder.matches(dto.getNewPassword(), user2.getPassword()));
    }

    @Test
    @Order(7)
    public void testDeleteSelf() throws Exception {
        mockMvc.perform(delete("/users/@self").header("Authorization", auth1))
                .andExpect(status().isNoContent());

        Optional<User> newUser1 = userRepository.findById(user1.getId());
        assertEquals(Optional.empty(), newUser1);
    }

    @Test
    @Order(8)
    public void testDeleteUserById() throws Exception {
        // User with role < moderator
        mockMvc.perform(delete("/users/{id}", user2.getId()))
                .andExpect(status().isForbidden());

        // User with role >= moderator
        mockMvc.perform(delete("/users/{id}", user2.getId())
                .header("Authorization", adminAuth))
                .andExpect(status().isNoContent());

        Optional<User> newUser2 = userRepository.findById(user2.getId());
        assertEquals(Optional.empty(), newUser2);
    }

}
