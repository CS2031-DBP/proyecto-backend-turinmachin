package com.turinmachin.unilife.post.infrastructure;

import com.turinmachin.unilife.post.domain.Post;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public class PostSpecifications {

    public static Specification<Post> hasUniversityId(UUID universityId) {
        if (universityId == null)
            return (root, query, cb) -> null;

        return (root, query, cb) -> cb.equal(root.get("university").get("id"), universityId);
    }

    public static Specification<Post> hasDegreeId(UUID degreeId) {
        if (degreeId == null)
            return (root, query, cb) -> null;

        return (root, query, cb) -> cb.equal(root.get("degree").get("id"), degreeId);
    }

    public static Specification<Post> hasAuthorId(UUID authorId) {
        if (authorId == null)
            return (root, query, cb) -> null;

        return (root, query, cb) -> cb.equal(root.get("author").get("id"), authorId);
    }

    public static Specification<Post> hasTags(List<String> tags) {
        if (tags == null || tags.isEmpty())
            return (root, query, cb) -> null;

        return (root, query, cb) -> root.join("tags", JoinType.LEFT).in(tags);
    }

}
