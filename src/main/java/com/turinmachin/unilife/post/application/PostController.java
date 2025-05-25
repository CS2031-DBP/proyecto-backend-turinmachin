package com.turinmachin.unilife.post.application;

import com.turinmachin.unilife.comment.domain.Comment;
import com.turinmachin.unilife.comment.domain.CommentService;
import com.turinmachin.unilife.comment.dto.CommentResponseDto;
import com.turinmachin.unilife.comment.dto.CreateCommentDto;
import com.turinmachin.unilife.comment.dto.UpdateCommentDto;
import com.turinmachin.unilife.comment.exception.CommentNotFoundException;
import com.turinmachin.unilife.common.domain.ListMapper;
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

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_USER')")
    public CommentResponseDto createPostComment(@PathVariable UUID id, @Valid @RequestBody CreateCommentDto dto,
                                                Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        Post post = postService.getPostById(id).orElseThrow(PostNotFoundException::new);
        Comment comment = commentService.createComment(dto, user, post);

        return modelMapper.map(comment, CommentResponseDto.class);
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

    @PutMapping("/{id}/comments/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public CommentResponseDto updatePostComment(@PathVariable UUID id, @PathVariable UUID commentId,
                                                @Valid @RequestBody UpdateCommentDto dto,
                                                Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        Comment comment = commentService.getPostCommentById(id, commentId).orElseThrow(CommentNotFoundException::new);

        if (!user.equals(comment.getAuthor())) {
            throw new ForbiddenException();
        }

        Comment updatedComment = commentService.updateComment(comment, dto);
        return modelMapper.map(updatedComment, CommentResponseDto.class);
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

    @DeleteMapping("/{id}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public void deletePostComment(@PathVariable UUID id, @PathVariable UUID commentId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        Comment comment = commentService
                .getPostCommentById(id, commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (user.getRole() == Role.USER && !user.equals(comment.getAuthor())) {
            throw new ForbiddenException();
        }

        commentService.deleteComment(comment);
    }

}
