package com.turinmachin.unilife.post.infrastructure;

import com.turinmachin.unilife.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID>, JpaSpecificationExecutor<Post> {

    void deleteByUniversityId(UUID universityId);

}
