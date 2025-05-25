package com.turinmachin.unilife.university.infrastructure;

import com.turinmachin.unilife.university.domain.University;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UniversityRepository extends JpaRepository<University, UUID> {

    boolean existsByName(String name);

}
