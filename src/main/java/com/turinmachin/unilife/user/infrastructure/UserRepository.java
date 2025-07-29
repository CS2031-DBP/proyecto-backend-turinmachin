package com.turinmachin.unilife.user.infrastructure;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;

import com.turinmachin.unilife.university.domain.University;
import com.turinmachin.unilife.user.domain.Role;
import com.turinmachin.unilife.user.domain.User;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @NativeQuery("SELECT * FROM users WHERE username = ?1 OR email = ?1 LIMIT 1")
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    Optional<User> findByVerificationId(UUID verificationId);

    Optional<User> findByPasswordResetTokenValueAndPasswordResetTokenCreatedAtGreaterThan(String passwordResetTokenId,
            Instant createdAt);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);

    boolean existsByRoleAndIdNot(Role role, UUID id);

    List<User> findAllByUniversity(University university);

    List<User> findAllByVerificationIdIsNullAndUniversityIsNull();

    Page<User> findAllByOrderByUsername(Pageable pageable);

    @Modifying
    @NativeQuery("UPDATE users SET university_id = NULL, degree_id = NULL WHERE university_id = :universityId")
    int detachUniversity(UUID universityId);

    @Modifying
    @NativeQuery("UPDATE users SET degree_id = NULL WHERE university_id = :universityId AND degree_id = :degreeId")
    int syncDegreeRemoval(UUID universityId, UUID degreeId);

    @NativeQuery("""
            SELECT * FROM users
            WHERE
                :excludedId IS NULL OR id <> :excludedId
                AND (
                    username % :query
                    OR display_name % :query
                    OR LOWER(username) LIKE '%' || LOWER(:query) || '%'
                    OR LOWER(display_name) LIKE '%' || LOWER(:query) || '%'
                )
            ORDER BY
                similarity(username, :query) DESC,
                similarity(display_name, :query) DESC,
                username
            """)
    Page<User> searchExcluding(String query, UUID excludedId, Pageable pageable);

    Page<User> findByIdNotOrderByUsername(UUID excludedId, Pageable pageable);

}
