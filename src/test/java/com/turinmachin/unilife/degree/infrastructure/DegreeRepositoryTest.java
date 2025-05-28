package com.turinmachin.unilife.degree.infrastructure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.turinmachin.unilife.PostgresContainerConfig;
import com.turinmachin.unilife.degree.domain.Degree;

@DataJpaTest
@Testcontainers
@Import(PostgresContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DegreeRepositoryTest {

    @Autowired
    private DegreeRepository degreeRepository;

    private Degree degree1;
    private Degree degree2;

    @BeforeEach
    public void setup() {
        degree1 = new Degree();
        degree1.setName("Computer Science");
        degree1 = degreeRepository.save(degree1);

        degree2 = new Degree();
        degree2.setName("Mechanical Engineering");
        degree2 = degreeRepository.save(degree2);
    }

    @Test
    public void testExistsByName() {
        Assertions.assertTrue(degreeRepository.existsByName(degree1.getName()));
        Assertions.assertTrue(degreeRepository.existsByName(degree2.getName()));
        Assertions.assertFalse(degreeRepository.existsByName("Pure Math"));
    }

}
