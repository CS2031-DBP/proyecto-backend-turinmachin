package com.turinmachin.unilife.degree.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

import com.turinmachin.unilife.degree.domain.Degree;
import com.turinmachin.unilife.degree.dto.DegreeWithStatsDto;

public interface DegreeRepository extends JpaRepository<Degree, UUID> {

    boolean existsByName(String name);

    boolean existsByShortName(String shortName);

    boolean existsByNameAndIdNot(String name, UUID id);

    boolean existsByShortNameAndIdNot(String shortName, UUID id);

    List<Degree> findByUniversitiesIdOrderByName(UUID universityId);

    List<Degree> findAllByOrderByName();

    @NativeQuery("""
            SELECT
                D.id,
                D.name,
                D.short_name,
                COUNT(DISTINCT UD.universities_id) as total_universities,
                COUNT(DISTINCT U.id) as total_students
            FROM degree D
                LEFT JOIN users U ON U.degree_id = :id
                LEFT JOIN university_degrees UD ON UD.degrees_id = :id
            WHERE D.id = :id
            GROUP BY D.id
            """)
    Optional<DegreeWithStatsDto> findWithStatsById(UUID id);

}
