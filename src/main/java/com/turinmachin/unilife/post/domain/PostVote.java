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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PostVote other = (PostVote) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
