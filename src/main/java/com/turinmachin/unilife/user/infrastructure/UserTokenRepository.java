package com.turinmachin.unilife.user.infrastructure;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.turinmachin.unilife.user.domain.UserToken;

public interface UserTokenRepository extends JpaRepository<UserToken, UUID> {

    boolean existsByValue(String value);

}
