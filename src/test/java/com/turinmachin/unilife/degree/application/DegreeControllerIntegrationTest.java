package com.turinmachin.unilife.degree.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.turinmachin.unilife.PostgresContainerConfig;
import com.turinmachin.unilife.degree.domain.Degree;
import com.turinmachin.unilife.degree.domain.DegreeService;
import com.turinmachin.unilife.degree.dto.CreateDegreeDto;
import com.turinmachin.unilife.degree.infrastructure.DegreeRepository;
import com.turinmachin.unilife.jwt.domain.JwtService;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import com.turinmachin.unilife.user.dto.RegisterUserDto;
import com.turinmachin.unilife.user.infrastructure.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(PostgresContainerConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class DegreeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DegreeService degreeService;

    @Autowired
    private UserService userService;

    @Autowired
    private DegreeRepository degreeRepository;

    private User admin;
    private User user;
    private String adminAuth;
    private String userAuth;
    private Degree degree1;
    private Degree degree2;

    @Test
    @Order(3)
    public void CreateDegreeTest() throws Exception {

        RegisterUserDto userDto = new RegisterUserDto();
        userDto.setUsername("juan");
        userDto.setEmail("juan@mail.com");
        userDto.setPassword("1234");
        userDto.setDisplayName("Juan");
        user = userService.createUser(userDto);
        user = userService.verifyUser(user);

        userAuth = "Bearer " + jwtService.generateToken(user);

        CreateDegreeDto degreeDto = new CreateDegreeDto();
        degreeDto.setName("SI");

        mockMvc.perform(post("/degrees")
                .header("Authorization", userAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(degreeDto)))
                .andExpect(status().isForbidden());

        admin = userRepository.findAll().getFirst();
        assertNotNull(admin);
        adminAuth = "Bearer " + jwtService.generateToken(admin);

        MvcResult result = mockMvc.perform(post("/degrees")
                .header("Authorization", adminAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(degreeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(degreeDto.getName()))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        UUID id = UUID.fromString(JsonPath.parse(body).read("$.id"));

        Optional<Degree> createdDegree = degreeRepository.findById(id);
        Assertions.assertTrue(createdDegree.isPresent());

    }

}
