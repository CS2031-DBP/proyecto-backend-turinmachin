package com.turinmachin.unilife.post.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class PostVoteId implements Serializable {

    private UUID postId;
    private UUID authorId;

    @Override
    public int hashCode() {
        return Objects.hash(postId, authorId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        PostVoteId objectId = (PostVoteId) obj;
        return postId.equals(objectId.postId) && authorId.equals(objectId.authorId);
    }

}
