package com.turinmachin.unilife.comment.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.turinmachin.unilife.perspective.domain.PerspectiveService;
import com.turinmachin.unilife.perspective.exception.ToxicContentException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.turinmachin.unilife.comment.dto.CreateCommentDto;
import com.turinmachin.unilife.comment.dto.UpdateCommentDto;
import com.turinmachin.unilife.comment.infrastructure.CommentRepository;
import com.turinmachin.unilife.post.domain.Post;
import com.turinmachin.unilife.user.domain.User;

import jakarta.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final ModelMapper modelMapper;

    private final PerspectiveService perspectiveService;

    public List<Comment> getAllPostComments(final UUID postId) {
        return commentRepository.findByPostIdAndParentNullOrderByCreatedAtDesc(postId);
    }

    public int countPostComments(final UUID postId) {
        return commentRepository.countByPostId(postId);
    }

    public Optional<Comment> getPostCommentById(final UUID postId, final UUID id) {
        return commentRepository.findByIdAndPostId(id, postId);
    }

    public Comment createComment(final CreateCommentDto commentDto, final User author, final Post post) {

        if (perspectiveService.isToxic(commentDto.getContent())) {
            throw new ToxicContentException();
        }

        final Comment comment = modelMapper.map(commentDto, Comment.class);
        comment.setAuthor(author);
        comment.setPost(post);

        return commentRepository.save(comment);
    }

    public Comment createCommentReply(final CreateCommentDto commentDto, final User author, final Comment parent) {

        if (perspectiveService.isToxic(commentDto.getContent())) {
            throw new ToxicContentException();
        }

        final Comment comment = modelMapper.map(commentDto, Comment.class);
        comment.setAuthor(author);
        comment.setPost(parent.getPost());
        comment.setParent(parent);

        return commentRepository.save(comment);
    }

    public Comment updateComment(final Comment comment, final UpdateCommentDto update) {
        comment.setContent(update.getContent());
        return commentRepository.save(comment);
    }

    public void deleteComment(final Comment comment) {
        commentRepository.delete(comment);
    }

}
