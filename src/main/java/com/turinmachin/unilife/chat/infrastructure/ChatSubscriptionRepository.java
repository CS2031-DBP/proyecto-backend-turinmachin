package com.turinmachin.unilife.chat.infrastructure;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.turinmachin.unilife.chat.domain.ChatSubscription;

public interface ChatSubscriptionRepository extends JpaRepository<ChatSubscription, UUID> {

    public List<ChatSubscription> findByUserId(UUID userId);

    public boolean existsByEndpoint(String endpoint);

}
