package com.turinmachin.unilife.university.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

import com.turinmachin.unilife.university.domain.University;
import com.turinmachin.unilife.university.dto.UniversityWithStatsDto;

public interface UniversityRepository extends JpaRepository<University, UUID> {

    boolean existsByName(String name);

    boolean existsByShortName(String shortName);

    boolean existsByNameAndIdNot(String name, UUID id);

    boolean existsByShortNameAndIdNot(String shortName, UUID id);

    Page<University> findByActiveTrueOrderByName(Pageable pageable);

    @NativeQuery("""
            SELECT * FROM university
            WHERE
                active IS TRUE
                AND (
                    name_tsv @@ plainto_tsquery('spanish', :query)
                    OR name % :query
                    OR short_name_tsv @@ plainto_tsquery('spanish', :query)
                    OR short_name % :query
                    OR LOWER(name) LIKE '%' || LOWER(:query) || '%'
                    OR LOWER(short_name) LIKE '%' || LOWER(:query) || '%'
                )
            ORDER BY
                ts_rank(name_tsv, plainto_tsquery('spanish', :query)) DESC,
                ts_rank(short_name_tsv, plainto_tsquery('spanish', :query)) DESC,
                similarity(name, :query) DESC,
                similarity(short_name, :query) DESC,
                name
            """)
    Page<University> omnisearch(String query, Pageable pageable);

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
