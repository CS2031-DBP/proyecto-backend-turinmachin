package com.turinmachin.unilife.university.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.turinmachin.unilife.degree.domain.Degree;
import com.turinmachin.unilife.post.domain.Post;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import lombok.Data;

@Data
@Entity
public class University {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = true)
    private String websiteUrl;

    // These EAGERs are needed to prevent an error
    // https://stackoverflow.com/questions/11746499/how-to-solve-the-failed-to-lazily-initialize-a-collection-of-role-hibernate-ex
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> emailDomains = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Degree> degrees = new ArrayList<>();

    // If a university is deleted, all its associated posts will be deleted too
    @OneToMany(mappedBy = "university", cascade = CascadeType.ALL)
    @OrderBy("createdAt DESC")
    private List<Post> posts = new ArrayList<>();

    @Column(nullable = false)
    private Boolean active = true;

    public boolean ownsEmail(String email) {
        String domain = email.substring(email.indexOf('@') + 1);
        return emailDomains.contains(domain);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        University that = (University) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
