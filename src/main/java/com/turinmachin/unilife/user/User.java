package com.turinmachin.unilife.user;

import com.turinmachin.unilife.degree.Degree;
import com.turinmachin.unilife.image.Image;
import com.turinmachin.unilife.post.Post;
import com.turinmachin.unilife.university.University;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    private String displayName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private UUID verificationId;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean verified = false;

    @Column
    private String bio;

    @ManyToOne
    @JoinColumn
    private University university;

    @ManyToOne
    @JoinColumn
    private Degree degree;

    @ManyToOne
    @JoinColumn
    private Image profilePicture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @OrderBy("createdAt DESC")
    private List<Post> posts = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    // Agregar lógica de pre persist, para comprobar que un usuario sin universidad no pueda estar en una carrera.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

}
