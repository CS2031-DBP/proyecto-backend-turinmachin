package com.turinmachin.unilife.user.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    Optional<User> findByPasswordResetTokenValue(String passwordResetTokenId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);

    boolean existsByRoleAndIdNot(Role role, UUID id);

    List<User> findAllByUniversity(University university);

    List<User> findAllByVerificationIdIsNullAndUniversityIsNull();

    @Modifying
    @NativeQuery("UPDATE users SET university_id = NULL, degree_id = NULL WHERE university_id = :universityId")
    int detachUniversity(UUID universityId);

    @Modifying
    @NativeQuery("UPDATE users SET degree_id = NULL WHERE university_id = :universityId AND degree_id = :degreeId")
    int syncDegreeRemoval(UUID universityId, UUID degreeId);

}
