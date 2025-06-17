package com.turinmachin.unilife.degree.infrastructure;

import com.turinmachin.unilife.degree.domain.Degree;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DegreeRepository extends JpaRepository<Degree, UUID> {

    boolean existsByName(String name);

    List<Degree> findByUniversitiesId(UUID universityId);

}
