package com.turinmachin.unilife.post;

import com.turinmachin.unilife.user.User;
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

}
