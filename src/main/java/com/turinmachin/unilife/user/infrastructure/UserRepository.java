package com.turinmachin.unilife.user.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;

import com.turinmachin.unilife.user.domain.Role;
import com.turinmachin.unilife.user.domain.User;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    @NativeQuery("SELECT * FROM users WHERE username = ?1 OR email = ?1 LIMIT 1")
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    Optional<User> findByVerificationId(UUID verificationId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);

    boolean existsByRoleAndIdNot(Role role, UUID id);

    @Modifying
    @NativeQuery("UPDATE users SET university_id = NULL, degree_id = NULL WHERE university_id = ?1")
    int detachUniversity(UUID universityId);

}
