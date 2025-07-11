package com.turinmachin.unilife.post.application;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.turinmachin.unilife.comment.domain.CommentService;
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
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping
    public Page<PostResponseDto> getAllPosts(
            @RequestParam(required = false) final String query,
            @RequestParam(required = false) final UUID universityId,
            @RequestParam(required = false) final UUID degreeId,
            @RequestParam(required = false) final List<String> tags,
            @RequestParam(required = false) final UUID authorId,
            final Pageable pageable) {
        Page<Post> posts;

        if (query == null) {
            final Specification<Post> spec = Specification
                    .where(PostSpecifications.hasUniversityId(universityId))
                    .and(PostSpecifications.hasDegreeId(degreeId))
                    .and(PostSpecifications.hasAuthorId(authorId))
                    .and(PostSpecifications.hasTags(tags))
                    .and(PostSpecifications.isActive());
            posts = postService.getPostsWithSpec(spec, pageable);
        } else {
            posts = postService.omnisearch(query, authorId, universityId, degreeId, tags, pageable);
        }

        return posts.map(post -> {
            final PostResponseDto response = modelMapper.map(post, PostResponseDto.class);
            response.setTotalComments(commentService.countPostComments(post.getId()));
            return response;
        });
    }

    @GetMapping("/{id}")
    public PostResponseDto getPost(@PathVariable final UUID id) {
        final Post post = postService.getActivePostById(id).orElseThrow(PostNotFoundException::new);

        final PostResponseDto response = modelMapper.map(post, PostResponseDto.class);
        response.setTotalComments(commentService.countPostComments(post.getId()));
        return response;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_USER')")
    public PostResponseDto createPost(@Valid @ModelAttribute final CreatePostDto dto,
            final Authentication authentication)
            throws IOException {
        final User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        final Post post = postService.createPost(dto, user);
        return modelMapper.map(post, PostResponseDto.class);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public PostResponseDto updatePost(@PathVariable final UUID id, @Valid @RequestBody final UpdatePostDto dto,
            final Authentication authentication) {
        final Post post = postService.getActivePostById(id).orElseThrow(PostNotFoundException::new);
        final User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        if (!user.equals(post.getAuthor())) {
            throw new ForbiddenException();
        }

        final Post updatedPost = postService.updatePost(post, dto);
        return modelMapper.map(updatedPost, PostResponseDto.class);
    }

    @PutMapping("/{id}/upvotes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public void addPostUpvote(@PathVariable final UUID id, final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        final Post post = postService.getActivePostById(id).orElseThrow(PostNotFoundException::new);
        postService.setPostVote(post, user, VoteType.UPVOTE);
    }

    @PutMapping("/{id}/downvotes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public void addPostDownvote(@PathVariable final UUID id, final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        final Post post = postService.getActivePostById(id).orElseThrow(PostNotFoundException::new);
        postService.setPostVote(post, user, VoteType.DOWNVOTE);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public void deletePost(@PathVariable final UUID id, final Authentication authentication) {
        final Post post = postService.getActivePostById(id).orElseThrow(PostNotFoundException::new);
        final User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        if (user.getRole() == Role.USER && !user.equals(post.getAuthor())) {
            throw new ForbiddenException();
        }

        postService.deactivatePost(post);
    }

    @DeleteMapping("/{id}/votes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public void deletePostVote(@PathVariable final UUID id, final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        final PostVote vote = postService
                .getPostVote(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Post or vote not found"));

        postService.removePostVote(vote);
    }

}
