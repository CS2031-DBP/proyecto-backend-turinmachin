package com.turinmachin.unilife.post.application;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.turinmachin.unilife.PostgresContainerConfig;
import com.turinmachin.unilife.jwt.domain.JwtService;
import com.turinmachin.unilife.post.domain.Post;
import com.turinmachin.unilife.post.domain.PostService;
import com.turinmachin.unilife.post.dto.CreatePostDto;
import com.turinmachin.unilife.post.dto.UpdatePostDto;
import com.turinmachin.unilife.post.infrastructure.PostRepository;
import com.turinmachin.unilife.university.domain.University;
import com.turinmachin.unilife.university.infrastructure.UniversityRepository;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import com.turinmachin.unilife.user.dto.RegisterUserDto;
import com.turinmachin.unilife.user.infrastructure.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Import(PostgresContainerConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private User admin;
    private User user1;
    private User user2;
    private String adminAuth;
    private String auth1;
    private String auth2;
    private Post post1;
    private Post post2;
    private Post post3;
    private Post post4;

    @Test
    @Order(1)
    public void testGetAllPosts() throws Exception {
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        University university = new University();
        university.setName("University 1");
        university.setWebsiteUrl("university.com");
        university = universityRepository.save(university);

        RegisterUserDto userDto1 = new RegisterUserDto();
        userDto1.setUsername("juan");
        userDto1.setEmail("juan@mail.com");
        userDto1.setPassword("1234");
        userDto1.setDisplayName("Juan");
        user1 = userService.createUser(userDto1);

        user1.setUniversity(university);
        user1 = userRepository.save(user1);

        RegisterUserDto userDto2 = new RegisterUserDto();
        userDto2.setUsername("carlos");
        userDto2.setEmail("carlos@mail.com");
        userDto2.setPassword("1234");
        userDto2.setDisplayName("Carlos");
        user2 = userService.createUser(userDto2);

        user2.setVerificationId(null);
        user2.setUniversity(university);
        user2 = userRepository.save(user2);

        CreatePostDto postDto1 = new CreatePostDto();
        postDto1.setContent("This is post 1");
        postDto1.setTags(new ArrayList<>());
        postDto1.setFiles(new ArrayList<>());
        post1 = postService.createPost(postDto1, user1);

        CreatePostDto postDto2 = new CreatePostDto();
        postDto2.setContent("This is post 2");
        postDto2.setTags(List.of("foo", "bar"));
        postDto2.setFiles(new ArrayList<>());
        post2 = postService.createPost(postDto2, user1);

        CreatePostDto postDto3 = new CreatePostDto();
        postDto3.setContent("This is post 3");
        postDto3.setTags(List.of("foo", "baz"));
        postDto3.setFiles(new ArrayList<>());
        post3 = postService.createPost(postDto3, user2);

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$.[0].id").value(post3.getId().toString()))
                .andExpect(jsonPath("$.[1].id").value(post2.getId().toString()))
                .andExpect(jsonPath("$.[2].id").value(post1.getId().toString()));

        mockMvc.perform(get("/posts")
                .param("authorId", user1.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(post2.getId().toString()))
                .andExpect(jsonPath("$.[1].id").value(post1.getId().toString()));

        mockMvc.perform(get("/posts")
                .param("authorId", user2.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.[0].id").value(post3.getId().toString()));
    }

    @Test
    @Order(2)
    public void testGetPostById() throws Exception {
        mockMvc.perform(get("/posts/{id}", post1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post1.getId().toString()));

        mockMvc.perform(get("/posts/{id}", post2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post2.getId().toString()));

        mockMvc.perform(get("/posts/{id}", post3.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post3.getId().toString()));
    }

    @Test
    @Order(3)
    public void testCreatePost() throws Exception {
        auth1 = "Bearer " + jwtService.generateToken(user1);

        // Unverified user
        mockMvc.perform(multipart("/posts")
                .header("Authorization", auth1)
                .param("content", "Hello, world!"))
                .andExpect(status().isForbidden());

        user1 = userService.verifyUser(user1);

        // Now as verified user
        MvcResult result = mockMvc.perform(multipart("/posts")
                .header("Authorization", auth1)
                .param("content", "Hello, world!")
                .param("tags", "tag1", "tag2"))
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        UUID id = UUID.fromString(JsonPath.parse(body).read("$.id"));

        Optional<Post> createdPost = postRepository.findById(id);
        Assertions.assertTrue(createdPost.isPresent());

        post4 = createdPost.get();
    }

    @Test
    @Order(4)
    public void testUpdatePost() throws Exception {
        UpdatePostDto dto = new UpdatePostDto();
        dto.setContent("This is some new content");
        dto.setTags(List.of("new-tag1", "new-tag2"));

        // Unauthenticated
        mockMvc.perform(put("/posts/{id}", post4.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        auth2 = "Bearer " + jwtService.generateToken(user2);

        // Authenticated, non-author user
        mockMvc.perform(put("/posts/{id}", post4.getId())
                .header("Authorization", auth2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        // Authenticated, author user
        mockMvc.perform(put("/posts/{id}", post4.getId())
                .header("Authorization", auth1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(dto.getContent()))
                .andExpect(jsonPath("$.tags.length()").value(dto.getTags().size()));
    }

    @Test
    @Order(5)
    public void testAddPostUpvote() throws Exception {
        // Unauthenticated
        mockMvc.perform(put("/posts/{id}/upvotes", post4.getId()))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/posts/{id}/upvotes", post4.getId())
                .header("Authorization", auth2))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/posts/{id}", post4.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(1));

        mockMvc.perform(put("/posts/{id}/upvotes", post4.getId())
                .header("Authorization", auth1))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/posts/{id}", post4.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(2));
    }

    @Test
    @Order(6)
    public void testAddPostDownvote() throws Exception {
        // Unauthenticated
        mockMvc.perform(put("/posts/{id}/downvotes", post4.getId()))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/posts/{id}/downvotes", post4.getId())
                .header("Authorization", auth2))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/posts/{id}", post4.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(0));

        mockMvc.perform(put("/posts/{id}/downvotes", post4.getId())
                .header("Authorization", auth1))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/posts/{id}", post4.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(-2));
    }

    @Test
    @Order(7)
    public void testDeletePostVote() throws Exception {
        // Unauthenticated
        mockMvc.perform(delete("/posts/{id}/votes", post4.getId()))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/posts/{id}/votes", post4.getId())
                .header("Authorization", auth2))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/posts/{id}", post4.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(-1));

        mockMvc.perform(delete("/posts/{id}/votes", post4.getId())
                .header("Authorization", auth1))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/posts/{id}", post4.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(0));
    }

    @Test
    @Order(8)
    public void testDeletePost() throws Exception {
        // Unauthenticated
        mockMvc.perform(delete("/posts/{id}", post4.getId()))
                .andExpect(status().isForbidden());

        // Authenticated, non-author user
        mockMvc.perform(delete("/posts/{id}", post4.getId())
                .header("Authorization", auth2))
                .andExpect(status().isForbidden());

        // Authenticated, author user
        mockMvc.perform(delete("/posts/{id}", post4.getId())
                .header("Authorization", auth1))
                .andExpect(status().isNoContent());

        admin = userRepository.findAll().getFirst();
        Assertions.assertNotNull(admin);
        adminAuth = "Bearer " + jwtService.generateToken(admin);

        // Authenticated, non-author, role >= moderator
        mockMvc.perform(delete("/posts/{id}", post1.getId())
                .header("Authorization", adminAuth))
                .andExpect(status().isNoContent());
    }

}
