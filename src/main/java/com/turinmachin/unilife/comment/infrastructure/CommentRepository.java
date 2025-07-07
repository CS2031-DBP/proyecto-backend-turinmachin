package com.turinmachin.unilife.comment.infrastructure;

import com.turinmachin.unilife.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findByPostId(UUID postId);

    List<Comment> findByPostIdAndParentNullOrderByCreatedAtDesc(UUID postId);

    Optional<Comment> findByIdAndPostId(UUID id, UUID postId);

    int countByPostId(UUID postId);

}
