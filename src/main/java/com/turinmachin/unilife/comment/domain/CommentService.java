package com.turinmachin.unilife.comment.domain;

import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turinmachin.unilife.comment.dto.CreateCommentDto;
import com.turinmachin.unilife.comment.dto.UpdateCommentDto;
import com.turinmachin.unilife.comment.infrastructure.CommentRepository;
import com.turinmachin.unilife.post.domain.Post;
import com.turinmachin.unilife.user.domain.User;

import jakarta.transaction.Transactional;

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

    public Comment createComment(CreateCommentDto commentDto, User author, Post post) {
        Comment comment = modelMapper.map(commentDto, Comment.class);
        comment.setAuthor(author);
        comment.setPost(post);

        return commentRepository.save(comment);
    }

    public Comment createCommentReply(CreateCommentDto commentDto, User author, Comment parent) {
        Comment comment = modelMapper.map(commentDto, Comment.class);
        comment.setAuthor(author);
        comment.setPost(parent.getPost());
        comment.setParent(parent);

        return commentRepository.save(comment);
    }

    public Comment updateComment(Comment comment, UpdateCommentDto update) {
        comment.setContent(update.getContent());
        return commentRepository.save(comment);
    }

    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }

}
