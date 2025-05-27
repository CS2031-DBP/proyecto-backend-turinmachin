package com.turinmachin.unilife.user.application;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turinmachin.unilife.PostgresContainerConfig;
import com.turinmachin.unilife.authentication.domain.AuthenticationService;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import com.turinmachin.unilife.user.dto.RegisterUserDto;
import com.turinmachin.unilife.user.infrastructure.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@Import(PostgresContainerConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    private AuthenticationService authenticationService;

    @Test
    @Order(1)
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.[0].role").value("ADMIN"));

        RegisterUserDto createUserDto = new RegisterUserDto();
        createUserDto.setUsername("juan");
        createUserDto.setEmail("juan@mail.com");
        createUserDto.setPassword("1234");
        userService.createUser(createUserDto);

        createUserDto = new RegisterUserDto();
        createUserDto.setUsername("carlos");
        createUserDto.setEmail("carlos@mail.com");
        createUserDto.setPassword("1234");
        userService.createUser(createUserDto);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    //self test

    @Test
    @Order(2)
    public void testGetUserById() throws Exception {
        User user = userRepository.findByUsername("juan").orElseThrow();
        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

}
