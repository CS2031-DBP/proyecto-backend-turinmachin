package com.turinmachin.unilife.post.application;

import com.turinmachin.unilife.comment.domain.Comment;
import com.turinmachin.unilife.comment.domain.CommentService;
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

}
