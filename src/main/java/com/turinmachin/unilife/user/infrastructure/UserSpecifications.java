package com.turinmachin.unilife.user.infrastructure;

import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.turinmachin.unilife.user.domain.User;

public class UserSpecifications {

    public static Specification<User> hasUniversityId(UUID universityId) {
        if (universityId == null)
            return (root, query, cb) -> null;

        return (root, query, cb) -> cb.equal(root.get("university").get("id"), universityId);
    }

    public static Specification<User> hasDegreeId(UUID degreeId) {
        if (degreeId == null)
            return (root, query, cb) -> null;

        return (root, query, cb) -> cb.equal(root.get("degree").get("id"), degreeId);
    }

}
