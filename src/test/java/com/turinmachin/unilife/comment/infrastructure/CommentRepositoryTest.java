package com.turinmachin.unilife.comment.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.turinmachin.unilife.PostgresContainerConfig;
import com.turinmachin.unilife.comment.domain.Comment;
import com.turinmachin.unilife.post.domain.Post;
import com.turinmachin.unilife.post.infrastructure.PostRepository;
import com.turinmachin.unilife.university.domain.University;
import com.turinmachin.unilife.university.infrastructure.UniversityRepository;
import com.turinmachin.unilife.user.domain.Role;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.infrastructure.UserRepository;

@DataJpaTest
@Testcontainers
@Import(PostgresContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UniversityRepository universityRepository;

    private University university;
    private User user;
    private Post post1;
    private Post post2;
    private Comment comment1;
    private Comment comment2;
    private Comment comment3;

    @BeforeEach
    public void setup() {
        university = new University();
        university.setEmailDomains(Set.of("uni1.com"));
        university.setName("University 1");
        university.setWebsiteUrl("https://website1.com");
        university = universityRepository.save(university);

        user = new User();
        user.setUsername("juan");
        user.setEmail("juan@uni1.com");
        user.setPassword("ts_should_be_hashed");
        user.setRole(Role.USER);
        user.setUniversity(university);
        user = userRepository.save(user);

        post1 = new Post();
        post1.setAuthor(user);
        post1.setUniversity(university);
        post1.setContent("Hello, world!");
        post1.setTags(List.of("foo", "bar"));
        post1 = postRepository.save(post1);

        post2 = new Post();
        post2.setAuthor(user);
        post2.setUniversity(university);
        post2.setContent("Hello, world, again!");
        post2.setTags(List.of("foo", "bar", "baz"));
        post2 = postRepository.save(post2);

        comment1 = new Comment();
        comment1.setAuthor(user);
        comment1.setPost(post1);
        comment1.setContent("Nice post");
        comment1 = commentRepository.save(comment1);

        comment2 = new Comment();
        comment2.setAuthor(user);
        comment2.setPost(post1);
        comment2.setContent("Nice post again");
        comment2 = commentRepository.save(comment2);

        comment3 = new Comment();
        comment3.setAuthor(user);
        comment3.setPost(post2);
        comment3.setContent("Nice post again again");
        comment3 = commentRepository.save(comment3);
    }

    @Test
    public void testGetByPostId() {
        List<Comment> result1 = commentRepository.findByPostId(post1.getId());
        Assertions.assertEquals(2, result1.size());
        Assertions.assertTrue(result1.contains(comment1));
        Assertions.assertTrue(result1.contains(comment2));

        List<Comment> result2 = commentRepository.findByPostId(post2.getId());
        Assertions.assertEquals(1, result2.size());
        Assertions.assertTrue(result2.contains(comment3));
    }

    @Test
    public void testGetByIdAndPostId() {
        Optional<Comment> result11 = commentRepository.findByIdAndPostId(comment1.getId(), post1.getId());
        Assertions.assertEquals(Optional.of(comment1), result11);

        Optional<Comment> result12 = commentRepository.findByIdAndPostId(comment1.getId(), post2.getId());
        Assertions.assertEquals(Optional.empty(), result12);

        Optional<Comment> result21 = commentRepository.findByIdAndPostId(comment2.getId(), post1.getId());
        Assertions.assertEquals(Optional.of(comment2), result21);

        Optional<Comment> result22 = commentRepository.findByIdAndPostId(comment2.getId(), post2.getId());
        Assertions.assertEquals(Optional.empty(), result22);

        Optional<Comment> result31 = commentRepository.findByIdAndPostId(comment3.getId(), post1.getId());
        Assertions.assertEquals(Optional.empty(), result31);

        Optional<Comment> result32 = commentRepository.findByIdAndPostId(comment3.getId(), post2.getId());
        Assertions.assertEquals(Optional.of(comment3), result32);
    }

}
