package com.turinmachin.unilife.university.domain;

import com.turinmachin.unilife.degree.domain.Degree;
import com.turinmachin.unilife.post.domain.Post;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class University {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String webisteUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> emailDomains = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Degree> degrees = new ArrayList<>();

    @OneToMany(mappedBy = "university", cascade = CascadeType.ALL)
    @OrderBy("createdAt DESC")
    private List<Post> posts = new ArrayList<>();

}
