package com.turinmachin.unilife.post.application;

import com.turinmachin.unilife.comment.domain.Comment;
import com.turinmachin.unilife.comment.domain.CommentService;
import com.turinmachin.unilife.comment.dto.CommentResponseDto;
import com.turinmachin.unilife.comment.exception.CommentNotFoundException;
import com.turinmachin.unilife.common.domain.ListMapper;
import com.turinmachin.unilife.post.domain.Post;
import com.turinmachin.unilife.post.domain.PostService;
import com.turinmachin.unilife.post.dto.CreatePostDto;
import com.turinmachin.unilife.post.dto.PostResponseDto;
import com.turinmachin.unilife.post.exception.PostNotFoundException;
import com.turinmachin.unilife.post.infrastructure.PostSpecifications;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ListMapper listMapper;

    @GetMapping
    public List<PostResponseDto> getAllPosts(
            @RequestParam(required = false) UUID universityId,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) UUID authorId) {
        Specification<Post> spec = Specification
                .where(PostSpecifications.hasUniversityId(universityId))
                .and(PostSpecifications.hasAuthorId(authorId))
                .and(PostSpecifications.hasTags(tags));

        List<Post> posts = postService.getPostsWithSpec(spec);
        return listMapper.map(posts, PostResponseDto.class).toList();
    }

    @GetMapping("/{id}")
    public PostResponseDto getPost(@PathVariable UUID id) {
        Post post = postService.getPostById(id).orElseThrow(PostNotFoundException::new);
        return modelMapper.map(post, PostResponseDto.class);
    }

    @GetMapping("/{id}/comments")
    public List<CommentResponseDto> getPostComments(@PathVariable UUID id) {
        Post post = postService.getPostById(id).orElseThrow(PostNotFoundException::new);
        return listMapper.map(post.getComments(), CommentResponseDto.class).toList();
    }

    @GetMapping("/{id}/comments/{commentId}")
    public CommentResponseDto getPostComments(@PathVariable UUID id, @PathVariable UUID commentId) {
        Comment comment = commentService.getPostCommentById(id, commentId).orElseThrow(CommentNotFoundException::new);
        return modelMapper.map(comment, CommentResponseDto.class);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_USER')")
    public PostResponseDto createPost(@Valid @ModelAttribute CreatePostDto dto, Authentication authentication)
            throws IOException {
        User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        Post post = postService.createPost(dto, user);
        return modelMapper.map(post, PostResponseDto.class);
    }

}
