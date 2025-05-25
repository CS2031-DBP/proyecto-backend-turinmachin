package com.turinmachin.unilife.post.application;

import com.turinmachin.unilife.comment.domain.CommentService;
import com.turinmachin.unilife.common.domain.ListMapper;
import com.turinmachin.unilife.post.domain.Post;
import com.turinmachin.unilife.post.domain.PostService;
import com.turinmachin.unilife.post.dto.PostResponseDto;
import com.turinmachin.unilife.post.exception.PostNotFoundException;
import com.turinmachin.unilife.post.infrastructure.PostSpecifications;
import com.turinmachin.unilife.user.domain.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

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

}
