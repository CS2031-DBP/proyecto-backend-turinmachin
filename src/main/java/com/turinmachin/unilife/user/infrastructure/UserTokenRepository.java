package com.turinmachin.unilife.user.infrastructure;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.turinmachin.unilife.user.domain.UserToken;

public interface UserTokenRepository extends JpaRepository<UserToken, UUID> {

    public boolean existsByValueAndCreatedAtGreaterThan(String value, Instant createdAt);

}
