package com.turinmachin.unilife.post.domain;

import com.turinmachin.unilife.comment.domain.Comment;
import com.turinmachin.unilife.degree.domain.Degree;
import com.turinmachin.unilife.image.domain.Image;
import com.turinmachin.unilife.university.domain.University;
import com.turinmachin.unilife.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User author;

    @Column(nullable = false)
    private String content;

    @OneToMany
    @OrderColumn
    private List<Image> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(nullable = false)
    private University university;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Degree degree;

    @ElementCollection
    @OrderBy("tags")
    private List<String> tags = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostVote> votes = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

}
