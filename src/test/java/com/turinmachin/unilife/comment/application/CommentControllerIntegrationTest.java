package com.turinmachin.unilife.comment.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turinmachin.unilife.PostgresContainerConfig;
import com.turinmachin.unilife.comment.domain.Comment;
import com.turinmachin.unilife.comment.domain.CommentService;
import com.turinmachin.unilife.comment.dto.CreateCommentDto;
import com.turinmachin.unilife.jwt.domain.JwtService;
import com.turinmachin.unilife.post.domain.Post;
import com.turinmachin.unilife.post.domain.PostService;
import com.turinmachin.unilife.post.dto.CreatePostDto;
import com.turinmachin.unilife.post.infrastructure.PostRepository;
import com.turinmachin.unilife.university.domain.University;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(PostgresContainerConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CommentControllerIntegrationTest {

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
    private String userAuth1;
    private String userAuth2;
    private String auth1;
    private String auth2;
    private Post post1;
    private Post post2;
    private Comment comment1;
    private Comment comment2;
    private Comment comment3;
    private Comment comment4;
    @Autowired
    private CommentService commentService;

    @Test
    @Order(1)
    public void getPostComments() throws Exception {

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

        userAuth1 = "Bearer " + jwtService.generateToken(user1);

        RegisterUserDto userDto2 = new RegisterUserDto();
        userDto2.setUsername("carlos");
        userDto2.setEmail("carlos@mail.com");
        userDto2.setPassword("1234");
        userDto2.setDisplayName("Carlos");
        user2 = userService.createUser(userDto2);

        user2.setUniversity(university);
        user2 = userRepository.save(user2);

        userAuth2 = "Bearer " + jwtService.generateToken(user2);

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

        mockMvc.perform(get("/posts/{postId}/comments", post1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        CreateCommentDto commentDto1 = new CreateCommentDto();
        commentDto1.setContent("This is comment 1");
        comment1 = commentService.createComment(commentDto1, user1, post1);

        CreateCommentDto commentDto2 = new CreateCommentDto();
        commentDto2.setContent("This is comment 2");
        comment2 = commentService.createComment(commentDto2, user2, post1);

        CreateCommentDto commentDto3 = new CreateCommentDto();
        commentDto3.setContent("This is comment 3");
        comment3 = commentService.createComment(commentDto3, user1, post2);

        CreateCommentDto commentDto4 = new CreateCommentDto();
        commentDto4.setContent("This is comment 4");
        comment4 = commentService.createComment(commentDto4, user1, post2);

        mockMvc.perform(get("/posts/{postId}/comments", post1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(comment1.getId().toString()))
                .andExpect(jsonPath("$.[0].content").value(comment1.getContent()))
                .andExpect(jsonPath("$.[1].id").value(comment2.getId().toString()))
                .andExpect(jsonPath("$.[1].content").value(comment2.getContent()));

        mockMvc.perform(get("/posts/{postId}/comments", post2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(comment3.getId().toString()))
                .andExpect(jsonPath("$.[0].content").value(comment3.getContent()))
                .andExpect(jsonPath("$.[1].id").value(comment4.getId().toString()))
                .andExpect(jsonPath("$.[1].content").value(comment4.getContent()));

    }

    @Test
    @Order(2)
    public void getIndividualPostComments() throws Exception {

        mockMvc.perform(get("/posts/{postId}/comments/{id}", post1.getId(), comment1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment1.getId().toString()))
                .andExpect(jsonPath("$.content").value(comment1.getContent()));

        mockMvc.perform(get("/posts/{postId}/comments/{id}", post1.getId(), comment2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment2.getId().toString()))
                .andExpect(jsonPath("$.content").value(comment2.getContent()));

        mockMvc.perform(get("/posts/{postId}/comments/{id}", post2.getId(), comment3.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment3.getId().toString()))
                .andExpect(jsonPath("$.content").value(comment3.getContent()));

        mockMvc.perform(get("/posts/{postId}/comments/{id}", post2.getId(), comment4.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment4.getId().toString()))
                .andExpect(jsonPath("$.content").value(comment4.getContent()));

    }

}
