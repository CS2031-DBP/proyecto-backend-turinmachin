package com.turinmachin.unilife.post.application;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.turinmachin.unilife.PostgresContainerConfig;
import com.turinmachin.unilife.jwt.domain.JwtService;
import com.turinmachin.unilife.post.domain.Post;
import com.turinmachin.unilife.post.domain.PostService;
import com.turinmachin.unilife.post.dto.CreatePostDto;
import com.turinmachin.unilife.post.infrastructure.PostRepository;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import com.turinmachin.unilife.user.dto.RegisterUserDto;

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
    private PostRepository postRepository;

    @Autowired
    private JwtService jwtService;

    private User user1;
    private User user2;
    private String adminAuth;
    private String auth1;
    private String auth2;
    private Post post1;
    private Post post2;
    private Post post3;

    @Test
    @Order(1)
    public void testGetAllPosts() throws Exception {
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        RegisterUserDto userDto1 = new RegisterUserDto();
        userDto1.setUsername("juan");
        userDto1.setEmail("juan@mail.com");
        userDto1.setPassword("1234");
        userDto1.setDisplayName("Juan");
        user1 = userService.createUser(userDto1);

        RegisterUserDto userDto2 = new RegisterUserDto();
        userDto2.setUsername("carlos");
        userDto2.setEmail("carlos@mail.com");
        userDto2.setPassword("1234");
        userDto2.setDisplayName("Carlos");
        user2 = userService.createUser(userDto2);

        auth1 = "Bearer " + jwtService.generateToken(user1);
        auth2 = "Bearer " + jwtService.generateToken(user2);

        CreatePostDto postDto1 = new CreatePostDto();
        postDto1.setContent("This is post 1");
        postDto1.setTags(new ArrayList<>());
        postDto1.setImages(new ArrayList<>());
        post1 = postService.createPost(postDto1, user1);

        CreatePostDto postDto2 = new CreatePostDto();
        postDto2.setContent("This is post 2");
        postDto2.setTags(List.of("foo", "bar"));
        postDto2.setImages(new ArrayList<>());
        post2 = postService.createPost(postDto2, user1);

        CreatePostDto postDto3 = new CreatePostDto();
        postDto3.setContent("This is post 3");
        postDto3.setTags(List.of("foo", "baz"));
        postDto3.setImages(new ArrayList<>());
        post3 = postService.createPost(postDto3, user2);

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$.[0].id").value(post3.getId()))
                .andExpect(jsonPath("$.[1].id").value(post2.getId()))
                .andExpect(jsonPath("$.[2].id").value(post1.getId()));
    }

    // @Test
    // @Order(4)
    // public void testThing() throws Exception {
    // // Unverified user
    // mockMvc.perform(multipart("/posts")
    // .header("Authorization", auth1)
    // .param("content", "Hello, world!"))
    // .andExpect(status().isForbidden());
    //
    // user1 = userService.verifyUser(user1);
    //
    // // Now as verify user
    // mockMvc.perform(multipart("/posts")
    // .header("Authorization", auth1)
    // .param("content", "Hello, world!"))
    // .andExpect(status().isCreated());
    //
    // }

}
