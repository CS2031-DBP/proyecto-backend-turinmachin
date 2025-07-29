package com.turinmachin.unilife.post.infrastructure;

import com.turinmachin.unilife.post.domain.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.NativeQuery;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID>, JpaSpecificationExecutor<Post> {

    Optional<Post> findByIdAndActiveTrue(UUID id);

    @NativeQuery("""
            SELECT * FROM post
            WHERE
                active IS TRUE
                AND (:authorId IS NULL OR author_id = :authorId)
                AND (:universityId IS NULL OR university_id = :universityId)
                AND (:degreeId IS NULL OR degree_id = :degreeId)
                AND (
                    COALESCE(:tags, NULL) IS NULL
                    OR EXISTS (
                        SELECT 1 FROM post_tags pt
                        WHERE pt.post_id = post.id AND pt.tags IN (:tags)
                        LIMIT 1
                    )
                )
                AND (
                    content_tsv @@ plainto_tsquery('spanish', :query)
                    OR content % :query
                )
            ORDER BY
                ts_rank(content_tsv, plainto_tsquery('spanish', :query)) DESC,
                similarity(content, :query) DESC,
                created_at DESC;
            """)
    Page<Post> search(String query, UUID authorId, UUID universityId, UUID degreeId, List<String> tags,
            Pageable pageable);

    @NativeQuery("""
            SELECT P.* FROM post P
            WHERE
                active = TRUE
                AND EXISTS (
                SELECT 1 FROM post_vote PV
                WHERE PV.post_id = P.id AND PV.author_id = :userId AND PV.value = 1
                LIMIT 1
            )
            ORDER BY created_at DESC
            """)
    Page<Post> findUpvotedBy(UUID userId, Pageable pageable);

    boolean existsByActiveTrueAndAuthorIdAndCreatedAtBetween(UUID authorId, Instant start, Instant end);

}
