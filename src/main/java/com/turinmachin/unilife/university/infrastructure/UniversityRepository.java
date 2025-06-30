package com.turinmachin.unilife.university.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

import com.turinmachin.unilife.university.domain.University;
import com.turinmachin.unilife.university.dto.UniversityWithStatsDto;

public interface UniversityRepository extends JpaRepository<University, UUID> {

    boolean existsByName(String name);

    boolean existsByShortName(String shortName);

    boolean existsByNameAndIdNot(String name, UUID id);

    boolean existsByShortNameAndIdNot(String shortName, UUID id);

    List<University> findByActiveTrueOrderByName();

    Optional<University> findByIdAndActiveTrue(UUID id);

    Optional<University> findByEmailDomainsContaining(String emailDomain);

    @NativeQuery("""
            SELECT
                university.id,
                university.name,
                university.short_name,
                university.website_url,
                university.picture_id,
                COUNT(DISTINCT users.id) as total_students
            FROM university
                LEFT JOIN users ON users.university_id = university.id
            WHERE university.id = :id
            GROUP BY university.id
            """)
    Optional<UniversityWithStatsDto> findWithStatsById(UUID id);

}
