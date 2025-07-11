package com.turinmachin.unilife.comment.application;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.turinmachin.unilife.comment.domain.Comment;
import com.turinmachin.unilife.comment.domain.CommentService;
import com.turinmachin.unilife.comment.dto.CommentResponseDto;
import com.turinmachin.unilife.comment.dto.CreateCommentDto;
import com.turinmachin.unilife.comment.dto.UpdateCommentDto;
import com.turinmachin.unilife.comment.exception.CommentNotFoundException;
import com.turinmachin.unilife.common.exception.ForbiddenException;
import com.turinmachin.unilife.post.domain.Post;
import com.turinmachin.unilife.post.domain.PostService;
import com.turinmachin.unilife.post.exception.PostNotFoundException;
import com.turinmachin.unilife.user.domain.Role;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final PostService postService;

    private final UserService userService;

    private final ModelMapper modelMapper;

    @GetMapping
    public List<CommentResponseDto> getAllPostComments(@PathVariable final UUID postId) {
        final List<Comment> comments = commentService.getAllPostComments(postId);
        return comments.stream().map(thing -> modelMapper.map(thing, CommentResponseDto.class)).toList();
    }

    @GetMapping("/{id}")
    public CommentResponseDto getPostComment(@PathVariable final UUID postId, @PathVariable final UUID id) {
        final Comment comment = commentService.getPostCommentById(postId, id)
                .orElseThrow(CommentNotFoundException::new);
        return modelMapper.map(comment, CommentResponseDto.class);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_USER')")
    public CommentResponseDto createPostComment(@PathVariable final UUID postId,
            @Valid @RequestBody final CreateCommentDto dto,
            final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        final Post post = postService.getActivePostById(postId).orElseThrow(PostNotFoundException::new);
        final Comment comment = commentService.createComment(dto, user, post);

        return modelMapper.map(comment, CommentResponseDto.class);
    }

    @PostMapping("/{id}/replies")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_USER')")
    public CommentResponseDto createPostCommentReply(@PathVariable final UUID postId, @PathVariable final UUID id,
            @Valid @RequestBody final CreateCommentDto dto,
            final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        final Comment parent = commentService.getPostCommentById(postId, id).orElseThrow(CommentNotFoundException::new);
        final Comment comment = commentService.createCommentReply(dto, user, parent);

        return modelMapper.map(comment, CommentResponseDto.class);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public CommentResponseDto updatePostComment(@PathVariable final UUID postId, @PathVariable final UUID id,
            @Valid @RequestBody final UpdateCommentDto dto,
            final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        final Comment comment = commentService.getPostCommentById(postId, id)
                .orElseThrow(CommentNotFoundException::new);

        if (!user.equals(comment.getAuthor())) {
            throw new ForbiddenException();
        }

        final Comment updatedComment = commentService.updateComment(comment, dto);
        return modelMapper.map(updatedComment, CommentResponseDto.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public void deletePostComment(@PathVariable final UUID postId, @PathVariable final UUID id,
            final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        final Comment comment = commentService
                .getPostCommentById(postId, id)
                .orElseThrow(CommentNotFoundException::new);

        if (user.getRole() == Role.USER && !user.equals(comment.getAuthor())) {
            throw new ForbiddenException();
        }

        commentService.deleteComment(comment);
    }

}
