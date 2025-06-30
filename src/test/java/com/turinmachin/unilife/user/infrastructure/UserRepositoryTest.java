package com.turinmachin.unilife.user.infrastructure;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
import com.turinmachin.unilife.university.infrastructure.UniversityRepository;
import com.turinmachin.unilife.user.domain.Role;
import com.turinmachin.unilife.user.domain.User;

@DataJpaTest
@Testcontainers
@Import(PostgresContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UniversityRepository universityRepository;

    User user1;
    User user2;
    User user3;
    University university1;
    University university2;

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

        user1 = new User();
        user1.setUsername("juan");
        user1.setEmail("juan@uni1.com");
        user1.setPassword("ts_should_be_hashed");
        user1.setRole(Role.USER);
        user1.setVerificationId(UUID.randomUUID());
        user1.setUniversity(university1);
        user1 = userRepository.save(user1);

        user2 = new User();
        user2.setUsername("carlos");
        user2.setEmail("carlos@uni1.com");
        user2.setPassword("ts_should_be_hashed_too");
        user2.setRole(Role.USER);
        user2.setVerificationId(UUID.randomUUID());
        user2.setUniversity(university1);
        user2 = userRepository.save(user2);

        user3 = new User();
        user3.setUsername("pancho");
        user3.setEmail("pancho@uni2.com");
        user3.setPassword("ts_should_be_hashed_too_also");
        user3.setRole(Role.ADMIN);
        user3.setUniversity(university2);
        user3 = userRepository.save(user3);
    }

    @Test
    public void testFindByUsername() {
        Optional<User> result1 = userRepository.findByUsername(user1.getUsername());
        Assertions.assertEquals(Optional.of(user1), result1);

        Optional<User> result2 = userRepository.findByUsername(user2.getUsername());
        Assertions.assertEquals(Optional.of(user2), result2);

        Optional<User> result3 = userRepository.findByUsername(user3.getUsername());
        Assertions.assertEquals(Optional.of(user3), result3);

        Optional<User> result4 = userRepository.findByUsername("bob");
        Assertions.assertEquals(Optional.empty(), result4);
    }

    @Test
    public void testFindByUsernameOrEmail() {
        Optional<User> result11 = userRepository.findByUsernameOrEmail(user1.getUsername());
        Optional<User> result12 = userRepository.findByUsernameOrEmail(user1.getEmail());
        Assertions.assertEquals(Optional.of(user1), result11);
        Assertions.assertEquals(Optional.of(user1), result12);

        Optional<User> result21 = userRepository.findByUsernameOrEmail(user2.getUsername());
        Optional<User> result22 = userRepository.findByUsernameOrEmail(user2.getEmail());
        Assertions.assertEquals(Optional.of(user2), result21);
        Assertions.assertEquals(Optional.of(user2), result22);

        Optional<User> result31 = userRepository.findByUsernameOrEmail(user3.getUsername());
        Optional<User> result32 = userRepository.findByUsernameOrEmail(user3.getEmail());
        Assertions.assertEquals(Optional.of(user3), result31);
        Assertions.assertEquals(Optional.of(user3), result32);

        Optional<User> result4 = userRepository.findByUsernameOrEmail("bob");
        Assertions.assertEquals(Optional.empty(), result4);
    }

    @Test
    public void testFindByVerificationId() {
        Optional<User> result1 = userRepository.findByVerificationId(user1.getVerificationId());
        Assertions.assertEquals(Optional.of(user1), result1);

        Optional<User> result2 = userRepository.findByVerificationId(user2.getVerificationId());
        Assertions.assertEquals(Optional.of(user2), result2);

        UUID nonExistentId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        Optional<User> result4 = userRepository.findByVerificationId(nonExistentId);
        Assertions.assertEquals(Optional.empty(), result4);
    }

    @Test
    public void testExistsByUsername() {
        Assertions.assertTrue(userRepository.existsByUsername(user1.getUsername()));
        Assertions.assertTrue(userRepository.existsByUsername(user2.getUsername()));
        Assertions.assertTrue(userRepository.existsByUsername(user3.getUsername()));
        Assertions.assertFalse(userRepository.existsByUsername("bob"));
    }

    @Test
    public void testExistsByEmail() {
        Assertions.assertTrue(userRepository.existsByEmail(user1.getEmail()));
        Assertions.assertTrue(userRepository.existsByEmail(user2.getEmail()));
        Assertions.assertTrue(userRepository.existsByEmail(user3.getEmail()));
        Assertions.assertFalse(userRepository.existsByEmail("bob@gmail.com"));
    }

    @Test
    public void testExistsByRole() {
        Assertions.assertTrue(userRepository.existsByRole(Role.USER));
        Assertions.assertFalse(userRepository.existsByRole(Role.MODERATOR));
        Assertions.assertTrue(userRepository.existsByRole(Role.ADMIN));
    }

    @Test
    public void testExistsByRoleAndIdNot() {
        Assertions.assertTrue(userRepository.existsByRoleAndIdNot(Role.USER, user1.getId()));
        Assertions.assertTrue(userRepository.existsByRoleAndIdNot(Role.USER, user2.getId()));
        Assertions.assertTrue(userRepository.existsByRoleAndIdNot(Role.USER, user3.getId()));

        Assertions.assertFalse(userRepository.existsByRoleAndIdNot(Role.MODERATOR, user1.getId()));
        Assertions.assertFalse(userRepository.existsByRoleAndIdNot(Role.MODERATOR, user2.getId()));
        Assertions.assertFalse(userRepository.existsByRoleAndIdNot(Role.MODERATOR, user3.getId()));

        Assertions.assertTrue(userRepository.existsByRoleAndIdNot(Role.ADMIN, user1.getId()));
        Assertions.assertTrue(userRepository.existsByRoleAndIdNot(Role.ADMIN, user2.getId()));
        Assertions.assertFalse(userRepository.existsByRoleAndIdNot(Role.ADMIN, user3.getId()));
    }

    @Test
    public void testDetachUniversity() {
        int affectedCount = userRepository.detachUniversity(university1.getId());
        Assertions.assertEquals(2, affectedCount);
    }

}
