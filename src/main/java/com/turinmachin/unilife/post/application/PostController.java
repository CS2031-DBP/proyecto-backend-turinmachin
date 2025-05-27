package com.turinmachin.unilife.post.application;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.turinmachin.unilife.common.exception.ForbiddenException;
import com.turinmachin.unilife.common.exception.NotFoundException;
import com.turinmachin.unilife.post.domain.Post;
import com.turinmachin.unilife.post.domain.PostService;
import com.turinmachin.unilife.post.domain.PostVote;
import com.turinmachin.unilife.post.domain.VoteType;
import com.turinmachin.unilife.post.dto.CreatePostDto;
import com.turinmachin.unilife.post.dto.PostResponseDto;
import com.turinmachin.unilife.post.dto.UpdatePostDto;
import com.turinmachin.unilife.post.exception.PostNotFoundException;
import com.turinmachin.unilife.post.infrastructure.PostSpecifications;
import com.turinmachin.unilife.user.domain.Role;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

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
        return posts.stream().map(post -> modelMapper.map(post, PostResponseDto.class)).toList();
    }

    public void foo() {
    }

    @GetMapping("/{id}")
    public PostResponseDto getPost(@PathVariable UUID id) {
        Post post = postService.getPostById(id).orElseThrow(PostNotFoundException::new);
        return modelMapper.map(post, PostResponseDto.class);
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

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public PostResponseDto updatePost(@PathVariable UUID id, @Valid @RequestBody UpdatePostDto dto,
            Authentication authentication) {
        Post post = postService.getPostById(id).orElseThrow(PostNotFoundException::new);
        User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        if (!user.equals(post.getAuthor())) {
            throw new ForbiddenException();
        }

        Post updatedPost = postService.updatePost(post, dto);
        return modelMapper.map(updatedPost, PostResponseDto.class);
    }

    @PutMapping("/{id}/upvotes")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void addPostUpvote(@PathVariable UUID id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        Post post = postService.getPostById(id).orElseThrow(PostNotFoundException::new);
        postService.setPostVote(post, user, VoteType.UPVOTE);
    }

    @PutMapping("/{id}/downvotes")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void addPostDownvote(@PathVariable UUID id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        Post post = postService.getPostById(id).orElseThrow(PostNotFoundException::new);
        postService.setPostVote(post, user, VoteType.DOWNVOTE);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public void deletePost(@PathVariable UUID id, Authentication authentication) {
        Post post = postService.getPostById(id).orElseThrow(PostNotFoundException::new);
        User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        if (user.getRole() == Role.USER && !user.equals(post.getAuthor())) {
            throw new ForbiddenException();
        }

        postService.deletePost(post);
    }

    @DeleteMapping("/{id}/votes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public void deletePostVote(@PathVariable UUID id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        PostVote vote = postService
                .getPostVote(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Post or vote not found"));

        postService.removePostVote(vote);
    }

}
