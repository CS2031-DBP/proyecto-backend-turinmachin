package com.turinmachin.unilife.post.domain;

import com.turinmachin.unilife.post.infrastructure.VoteTypeConverter;
import com.turinmachin.unilife.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Data;

import java.util.Objects;

@Data
@Entity
public class PostVote {

    @EmbeddedId
    private PostVoteId id;

    @MapsId("postId")
    @ManyToOne
    @JoinColumn(nullable = false)
    private Post post;

    @MapsId("authorId")
    @ManyToOne
    @JoinColumn(nullable = false)
    private User author;

    @Convert(converter = VoteTypeConverter.class)
    @Column(nullable = false)
    private VoteType value;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PostVote postVote = (PostVote) o;
        return Objects.equals(id, postVote.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
