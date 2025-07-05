package com.turinmachin.unilife.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.turinmachin.unilife.user.domain.Role;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import com.turinmachin.unilife.user.dto.RegisterUserDto;

@Configuration
public class BootstrapConfiguration {

    private final Logger logger = LoggerFactory.getLogger(BootstrapConfiguration.class);

    @Value("${bootstrap.admin.email}")
    private String adminEmail;

    @Value("${bootstrap.admin.username}")
    private String adminUsername;

    @Value("${bootstrap.admin.password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner adminBootstrapper(UserService userService) {
        return args -> {
            if (userService.userExistsByRole(Role.ADMIN)) {
                return;
            }

            logger.info("No admin users found. Creating default admin user");

            RegisterUserDto dto = new RegisterUserDto();
            dto.setEmail(adminEmail);
            dto.setUsername(adminUsername);
            dto.setPassword(adminPassword);
            User admin = userService.createUser(dto);
            userService.updateUserRole(admin, Role.ADMIN);
            userService.verifyUser(admin);
        };
    }

    @Bean
    public FlywayMigrationStrategy repairFlyway() {
        return flyway -> {
            flyway.repair();
            flyway.migrate();
        };
    }

}
