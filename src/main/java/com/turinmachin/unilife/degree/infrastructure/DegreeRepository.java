package com.turinmachin.unilife.degree.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<Degree> findByUniversitiesIdOrderByName(UUID universityId, Pageable pageable);

    List<Degree> findAllByOrderByName();

    Page<Degree> findAllByOrderByName(Pageable Pageable);

    @NativeQuery("""
            SELECT D.*
            FROM degree D
            WHERE
                (:universityId IS NULL OR EXISTS (
                    SELECT 1 FROM university_degrees
                    WHERE universities_id = :universityId AND degrees_id = D.id
                ))
                AND (
                    D.name_tsv @@ plainto_tsquery('spanish', :query)
                    OR D.name % :query
                    OR D.short_name_tsv @@ plainto_tsquery('spanish', :query)
                    OR D.short_name % :query
                    OR LOWER(D.name) LIKE '%' || LOWER(:query) || '%'
                    OR LOWER(D.short_name) LIKE '%' || LOWER(:query) || '%'
                )
            ORDER BY
                ts_rank(D.name_tsv, plainto_tsquery('spanish', :query)) DESC,
                ts_rank(D.short_name_tsv, plainto_tsquery('spanish', :query)) DESC,
                similarity(D.name, :query) DESC,
                similarity(D.short_name, :query) DESC,
                D.name
            """)
    Page<Degree> search(String query, UUID universityId, Pageable pageable);

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
