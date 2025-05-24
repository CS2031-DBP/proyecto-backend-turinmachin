package com.turinmachin.unilife.comment.domain;

import com.turinmachin.unilife.comment.infrastructure.CommentRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Optional<Comment> getPostCommentById(UUID postId, UUID id) {
        return commentRepository.findByIdAndPostId(id, postId);
    }

}
