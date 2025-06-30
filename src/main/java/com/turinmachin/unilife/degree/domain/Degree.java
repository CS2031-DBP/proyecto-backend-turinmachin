package com.turinmachin.unilife.degree.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.turinmachin.unilife.university.domain.University;
import com.turinmachin.unilife.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Degree {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = true, unique = true)
    private String shortName;

    @ManyToMany(mappedBy = "degrees")
    private List<University> universities = new ArrayList<>();

    @OneToMany(mappedBy = "degree")
    private List<User> students = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        Degree degree = (Degree) o;
        return Objects.equals(id, degree.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
