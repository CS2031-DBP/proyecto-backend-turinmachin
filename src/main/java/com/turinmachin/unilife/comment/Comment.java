package com.turinmachin.unilife.comment;

import com.turinmachin.unilife.post.Post;
import com.turinmachin.unilife.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;

import lombok.Data;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User author;

    @Column(nullable = false)
    private String content;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant createAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

}
