package com.turinmachin.unilife.ai.infrastructure;

import com.turinmachin.unilife.ai.domain.AIMessage;
import com.turinmachin.unilife.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AIMessageRepository extends JpaRepository<AIMessage, UUID> {

    List<AIMessage> findAllByUserOrderByCreatedAtAsc(User user);

    void deleteAllByUser(User user);

}
