package com.turinmachin.unilife.post.infrastructure;

import com.turinmachin.unilife.post.domain.PostVote;
import com.turinmachin.unilife.post.domain.PostVoteId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostVoteRepository extends JpaRepository<PostVote, PostVoteId> {
}
