package com.turinmachin.unilife.university.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.turinmachin.unilife.PostgresContainerConfig;
import com.turinmachin.unilife.degree.domain.Degree;
import com.turinmachin.unilife.degree.domain.DegreeService;
import com.turinmachin.unilife.degree.dto.CreateDegreeDto;
import com.turinmachin.unilife.jwt.domain.JwtService;
import com.turinmachin.unilife.university.domain.University;
import com.turinmachin.unilife.university.domain.UniversityService;
import com.turinmachin.unilife.university.dto.AddDegreeToUniversityDto;
import com.turinmachin.unilife.university.dto.CreateUniversityDto;
import com.turinmachin.unilife.university.infrastructure.UniversityRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
public class UniversityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UniversityService universityService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UniversityRepository universityRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DegreeService degreeService;

    @Autowired
    private UserService userService;

    private User user;
    private User admin;
    private String userAuth;
    private String adminAuth;
    private Degree degree1;
    private Degree degree2;
    private Degree degree3;
    private University university1;
    private University university2;
    private University university3;


    @Test
    @Order(1)
    public void GetAllUniversitiesTest() throws Exception {
        mockMvc.perform(get("/universities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        CreateDegreeDto degreeDto1 = new CreateDegreeDto();
        degreeDto1.setName("CS");
        degree1 = degreeService.createDegree(degreeDto1);

        CreateDegreeDto degreeDto2 = new CreateDegreeDto();
        degreeDto2.setName("DS");
        degree2 = degreeService.createDegree(degreeDto2);

        CreateDegreeDto degreeDto3 = new CreateDegreeDto();
        degreeDto3.setName("SI");
        degree3 = degreeService.createDegree(degreeDto3);

        CreateUniversityDto createUniversityDto1 = new CreateUniversityDto();
        createUniversityDto1.setName("UTEC");
        createUniversityDto1.setEmailDomains(List.of("utec.edu.pe"));
        createUniversityDto1.setDegreeIds(List.of(degree1.getId(), degree2.getId()));
        university1 = universityService.createUniversity(createUniversityDto1);

        CreateUniversityDto createUniversityDto2 = new CreateUniversityDto();
        createUniversityDto2.setName("UTEC2");
        createUniversityDto2.setEmailDomains(List.of("utec2.edu.pe"));
        createUniversityDto2.setDegreeIds(List.of(degree1.getId(), degree2.getId(), degree3.getId()));
        university2 = universityService.createUniversity(createUniversityDto2);

        mockMvc.perform(get("/universities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

    }

    @Test
    @Order(2)
    public void GetUniversityByIdTest() throws Exception {
        mockMvc.perform(get("/universities/{id}", university1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(university1.getId().toString()))
                .andExpect(jsonPath("$.name").value(university1.getName()))
                .andExpect(jsonPath("$.emailDomains.length()").value(1))
                .andExpect(jsonPath("$.emailDomains[0]").value(university1.getEmailDomains().getFirst()))
                .andExpect(jsonPath("$.degrees.length()").value(2))
                .andExpect(jsonPath("$.degrees[0].id").value(degree1.getId().toString()))
                .andExpect(jsonPath("$.degrees[0].name").value(degree1.getName()))
                .andExpect(jsonPath("$.degrees[1].id").value(degree2.getId().toString()))
                .andExpect(jsonPath("$.degrees[1].name").value(degree2.getName()));

        mockMvc.perform(get("/universities/{id}", university2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(university2.getId().toString()))
                .andExpect(jsonPath("$.name").value(university2.getName()))
                .andExpect(jsonPath("$.emailDomains.length()").value(1))
                .andExpect(jsonPath("$.emailDomains[0]").value(university2.getEmailDomains().getFirst()))
                .andExpect(jsonPath("$.degrees.length()").value(3))
                .andExpect(jsonPath("$.degrees[0].id").value(degree1.getId().toString()))
                .andExpect(jsonPath("$.degrees[0].name").value(degree1.getName()))
                .andExpect(jsonPath("$.degrees[1].id").value(degree2.getId().toString()))
                .andExpect(jsonPath("$.degrees[1].name").value(degree2.getName()))
                .andExpect(jsonPath("$.degrees[2].id").value(degree3.getId().toString()))
                .andExpect(jsonPath("$.degrees[2].name").value(degree3.getName()));

    }

    @Test
    @Order(3)
    public void CreateUniversityTest() throws Exception {

        RegisterUserDto userDto = new RegisterUserDto();
        userDto.setUsername("juan");
        userDto.setEmail("juan@mail.com");
        userDto.setPassword("1234");
        userDto.setDisplayName("Juan");
        user = userService.createUser(userDto);
        user = userService.verifyUser(user);

        userAuth = "Bearer " + jwtService.generateToken(user);

        CreateUniversityDto createUniversityDto = new CreateUniversityDto();
        createUniversityDto.setName("UTEC3");
        createUniversityDto.setEmailDomains(List.of("utec3.edu.pe"));
        createUniversityDto.setDegreeIds(List.of(degree1.getId(), degree3.getId()));

        mockMvc.perform(post("/universities")
                        .header("Authorization", userAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUniversityDto)))
                .andExpect(status().isForbidden());

        admin = userRepository.findAll().getFirst();
        assertNotNull(admin);
        adminAuth = "Bearer " + jwtService.generateToken(admin);

        MvcResult result = mockMvc.perform(post("/universities")
                        .header("Authorization", adminAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUniversityDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(createUniversityDto.getName()))
                .andExpect(jsonPath("$.emailDomains.length()").value(1))
                .andExpect(jsonPath("$.emailDomains[0]").value(createUniversityDto.getEmailDomains().getFirst()))
                .andExpect(jsonPath("$.degrees.length()").value(2))
                .andExpect(jsonPath("$.degrees[0].id").value(degree1.getId().toString()))
                .andExpect(jsonPath("$.degrees[0].name").value(degree1.getName()))
                .andExpect(jsonPath("$.degrees[1].id").value(degree3.getId().toString()))
                .andExpect(jsonPath("$.degrees[1].name").value(degree3.getName()))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        UUID id = UUID.fromString(JsonPath.parse(body).read("$.id"));

        Optional<University> createdUniversity = universityRepository.findById(id);
        Assertions.assertTrue(createdUniversity.isPresent());

    }

    // Update university está incompleto, se deja el test pendiente

    @Test
    @Order(5)
    public void AddUniversityDegreeTest() throws Exception {
        AddDegreeToUniversityDto addDegreeToUniversityDto = new AddDegreeToUniversityDto();
        addDegreeToUniversityDto.setDegreeId(degree3.getId());

        mockMvc.perform(patch("/universities/{id}/degrees", university1.getId())
                .header("Authorization", userAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addDegreeToUniversityDto)))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/universities/{id}/degrees", university1.getId())
                .header("Authorization", adminAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addDegreeToUniversityDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(university1.getId().toString()))
                .andExpect(jsonPath("$.name").value(university1.getName()))
                .andExpect(jsonPath("$.degrees.length()").value(3));

    }

    @Test
    @Order(6)
    public void RemoveUniversityDegreeTest() throws Exception {

        mockMvc.perform(delete("/universities/{id}/degrees/{degreeId}", university1.getId(), degree3.getId())
                        .header("Authorization", userAuth))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/universities/{id}/degrees/{degreeId}", university1.getId(), degree3.getId())
                        .header("Authorization", adminAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(university1.getId().toString()))
                .andExpect(jsonPath("$.name").value(university1.getName()))
                .andExpect(jsonPath("$.degrees.length()").value(2));

    }

    @Test
    @Order(7)
    public void DeleteUniversityTest() throws Exception {
        mockMvc.perform(delete("/universities/{id}", university1.getId())
                        .header("Authorization", userAuth))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/universities/{id}", university1.getId()) // Con fines demostrativos, te queremos UTEC <3
                        .header("Authorization", adminAuth))
                .andExpect(status().isNoContent());

        assertEquals(false, university1.getActive());

    }

}
