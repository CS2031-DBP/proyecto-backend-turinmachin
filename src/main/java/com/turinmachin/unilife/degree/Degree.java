package com.turinmachin.unilife.degree;

import com.turinmachin.unilife.university.University;
import com.turinmachin.unilife.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GenerationType;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Degree {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "degrees")
    private List<University> universities = new ArrayList<>();

    @OneToMany(mappedBy = "degree")
    private List<User> students = new ArrayList<>();

}
