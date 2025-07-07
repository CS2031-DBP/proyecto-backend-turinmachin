package com.turinmachin.unilife.university.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.turinmachin.unilife.PostgresContainerConfig;
import com.turinmachin.unilife.university.domain.University;

@DataJpaTest
@Testcontainers
@Import(PostgresContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UniversityRepositoryTest {

    @Autowired
    private UniversityRepository universityRepository;

    private University university1;
    private University university2;
    private University university3;

    @BeforeEach
    public void setup() {
        university1 = new University();
        university1.setEmailDomains(Set.of("uni1.com"));
        university1.setName("University 1");
        university1.setWebsiteUrl("https://website1.com");
        university1 = universityRepository.save(university1);

        university2 = new University();
        university2.setEmailDomains(Set.of("uni2.com"));
        university2.setName("University 2");
        university2.setWebsiteUrl("https://website2.com");
        university2 = universityRepository.save(university2);

        university3 = new University();
        university3.setEmailDomains(Set.of("uni3.com"));
        university3.setName("University 3");
        university3.setWebsiteUrl("https://website3.com");
        university3.setActive(false);
        university3 = universityRepository.save(university3);
    }

    @Test
    public void testExistsByName() {
        Assertions.assertTrue(universityRepository.existsByName(university1.getName()));
        Assertions.assertTrue(universityRepository.existsByName(university2.getName()));
        Assertions.assertTrue(universityRepository.existsByName(university3.getName()));
        Assertions.assertFalse(universityRepository.existsByName("University 4"));
        Assertions.assertFalse(universityRepository.existsByName("University 1 "));
    }

    @Test
    public void testFindByIdAndActiveTrue() {
        Optional<University> result1 = universityRepository.findByIdAndActiveTrue(university1.getId());
        Assertions.assertEquals(Optional.ofNullable(university1), result1);

        Optional<University> result2 = universityRepository.findByIdAndActiveTrue(university2.getId());
        Assertions.assertEquals(Optional.ofNullable(university2), result2);

        Optional<University> result3 = universityRepository.findByIdAndActiveTrue(university3.getId());
        Assertions.assertEquals(Optional.empty(), result3);
    }

}
