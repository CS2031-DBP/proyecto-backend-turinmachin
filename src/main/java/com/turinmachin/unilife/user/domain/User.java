package com.turinmachin.unilife.user.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.turinmachin.unilife.authentication.domain.AuthProvider;
import com.turinmachin.unilife.degree.domain.Degree;
import com.turinmachin.unilife.fileinfo.domain.FileInfo;
import com.turinmachin.unilife.post.domain.Post;
import com.turinmachin.unilife.university.domain.University;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = true)
    private String displayName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Column(nullable = true, unique = true)
    private UUID verificationId;

    @Column(nullable = true)
    private String bio;

    @Column(nullable = true)
    private LocalDate birthday;

    @ManyToOne
    @JoinColumn(nullable = true)
    private University university;

    @ManyToOne
    @JoinColumn(nullable = true)
    private Degree degree;

    @ManyToOne
    @JoinColumn(nullable = true)
    private FileInfo profilePicture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("createdAt DESC")
    private List<Post> posts = new ArrayList<>();

    @Column(nullable = true)
    private Instant lastVerificationEmailSent;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private UserToken passwordResetToken;

    @Column(nullable = false)
    public Integer streakValue = 0;

    @Column(nullable = true)
    public LocalDate lastStreakDate;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public int getStreak() {
        final LocalDate today = LocalDate.now(ZoneOffset.UTC);

        if (lastStreakDate == null || lastStreakDate.isBefore(today.minusDays(1)))
            return 0;

        return streakValue;
    }

    public boolean getStreakSafe() {
        final LocalDate today = LocalDate.now(ZoneOffset.UTC);
        return streakValue == 0 || lastStreakDate.equals(today);
    }

    public boolean getVerified() {
        return verificationId == null;
    }

    public boolean getHasPassword() {
        return password != null;
    }

    @PrePersist
    public void prePersist() {
        if (degree != null && university == null) {
            if (university == null) {
                throw new IllegalStateException("User cannot have a degree without a university");
            }

            if (!university.getDegrees().contains(degree)) {
                throw new IllegalStateException("User has degree not in university");
            }
        }

        if (university != null && !university.ownsEmail(email)) {
            throw new IllegalStateException("User email is not their university's");
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        final User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
