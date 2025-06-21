package com.turinmachin.unilife.university.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.turinmachin.unilife.university.domain.University;

public interface UniversityRepository extends JpaRepository<University, UUID> {

    boolean existsByName(String name);

    boolean existsByShortName(String shortName);

    boolean existsByNameAndIdNot(String name, UUID id);

    boolean existsByShortNameAndIdNot(String shortName, UUID id);

    List<University> findByActiveTrue();

    Optional<University> findByIdAndActiveTrue(UUID id);

}
